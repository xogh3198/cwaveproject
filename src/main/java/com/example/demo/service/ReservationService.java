package com.example.demo.service;

import com.example.demo.domain.Reservation;
import com.example.demo.domain.ReservationStatus;
import com.example.demo.domain.Schedule;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import java.util.concurrent.TimeUnit;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final ScheduleRepository scheduleRepository;

    private static final String SEAT_KEY_PREFIX = "seat:performance:";

    /**
     * 1. PENDING 상태의 예매를 생성합니다. (DB 저장)
     * 실제 좌석 점유나 재고 감소는 하지 않습니다.
     */
    @Transactional
    public Reservation createPendingReservation(Long scheduleId, String userId, String seatCode) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다."));

        // 동시 요청으로 인한 중복 PENDING 예매 방지
        String seatKey = SEAT_KEY_PREFIX + scheduleId;
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(seatKey, seatCode))) {
            throw new IllegalStateException("이미 예매가 진행 중이거나 완료된 좌석입니다.");
        }
        
        Reservation reservation = new Reservation(userId, schedule, seatCode);
        return reservationRepository.save(reservation);
    }

    /**
     * 2. Kafka Consumer에 의해 호출될 예매 확정 로직입니다.
     * 분산 락을 통해 좌석을 점유하고, 재고를 감소시키며 예매 상태를 CONFIRMED로 변경합니다.
     */
    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다. ID: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            System.out.println("이미 처리된 예매입니다: " + reservationId);
            return;
        }
        
        Schedule schedule = reservation.getSchedule();
        Long scheduleId = schedule.getId();
        String seatCode = reservation.getSeatCode();

        String seatKey = SEAT_KEY_PREFIX + scheduleId;
        String lockKey = "lock:schedule:" + scheduleId + ":seat:" + seatCode;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("좌석 잠금 획득에 실패했습니다. 다음 기회에 처리됩니다.");
            }

            // 락 획득 후 다시 한번 좌석 점유 상태 확인
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(seatKey, seatCode))) {
                throw new IllegalStateException("이미 예매된 좌석입니다.");
            }
            
            if (schedule.getRemainingSeats() <= 0) {
                throw new IllegalStateException("잔여 좌석이 없습니다.");
            }

            schedule.setRemainingSeats(schedule.getRemainingSeats() - 1);
            scheduleRepository.save(schedule);

            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);

            // Redis에 좌석 점유 정보 기록
            redisTemplate.opsForSet().add(seatKey, seatCode);
            System.out.println("예매 확정 성공! Reservation ID: " + reservationId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("예매 처리 중 오류가 발생했습니다.", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            Schedule schedule = reservation.getSchedule();
            String seatCode = reservation.getSeatCode();
            schedule.setRemainingSeats(schedule.getRemainingSeats() + 1);
            scheduleRepository.save(schedule);

            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
            
            String seatKey = SEAT_KEY_PREFIX + schedule.getId();
            redisTemplate.opsForSet().remove(seatKey, seatCode);
            
            System.out.println("예매 취소 완료: 예매 ID " + reservationId + ", 좌석 복구 완료");
        });
    }
}