package com.example.demo.service;

import com.example.demo.domain.Reservation;
import com.example.demo.domain.Schedule;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;

    public ReservationService(ReservationRepository reservationRepository, ScheduleRepository scheduleRepository) {
        this.reservationRepository = reservationRepository;
        this.scheduleRepository = scheduleRepository;
    }

    // ▼▼▼ --- 수정된 부분 --- ▼▼▼
    @Transactional
    public Reservation reserveSeat(Long scheduleId, String userId, String seatCode) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다."));

        if (schedule.getRemainingSeats() <= 0) {
            throw new IllegalStateException("잔여 좌석이 없습니다.");
        }
        
        // 좌석 수 감소 (예매 시점에 좌석 선점)
        schedule.setRemainingSeats(schedule.getRemainingSeats() - 1);
        scheduleRepository.save(schedule);

        Reservation reservation = new Reservation(userId, schedule, seatCode);
        return reservationRepository.save(reservation);
    }
    // ▲▲▲ --- 수정된 부분 --- ▲▲▲

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // ▼▼▼ --- 추가된 부분 --- ▼▼▼
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 예매 ID로 예매 정보 조회
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            // 예매와 연결된 스케줄의 좌석 수를 1 증가
            Schedule schedule = reservation.getSchedule();
            schedule.setRemainingSeats(schedule.getRemainingSeats() + 1);
            scheduleRepository.save(schedule);

            // 예매 정보 삭제
            reservationRepository.delete(reservation);
            
            System.out.println("예매 취소 완료: 예매 ID " + reservationId + ", 좌석 복구 완료");
        });
    }
    // ▲▲▲ --- 추가된 부분 --- ▲▲▲
}
