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


// package com.example.demo.service;

// import com.example.demo.domain.Reservation;
// import com.example.demo.domain.Schedule;
// import com.example.demo.repository.ReservationRepository;
// import com.example.demo.repository.ScheduleRepository;
// import jakarta.persistence.EntityNotFoundException;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Service
// @RequiredArgsConstructor
// public class ReservationService {

//     private final ReservationRepository reservationRepository;
//     private final ScheduleRepository scheduleRepository; // ScheduleRepository 주입

//     @Transactional
//     public Reservation createReservation(String userId, Long scheduleId, String seatCode) {
//         // 1. 스케줄 정보 조회
//         Schedule schedule = scheduleRepository.findById(scheduleId)
//                 .orElseThrow(() -> new EntityNotFoundException("해당 스케줄을 찾을 수 없습니다. ID: " + scheduleId));

//         // 2. 남은 좌석 확인
//         if (schedule.getRemainingSeats() <= 0) {
//             throw new IllegalStateException("남은 좌석이 없습니다.");
//         }

//         // 3. 남은 좌석 수 감소
//         schedule.setRemainingSeats(schedule.getRemainingSeats() - 1);
//         scheduleRepository.save(schedule); // 변경된 좌석 수 저장

//         // 4. 예매 정보 생성 및 저장
//         Reservation reservation = new Reservation(userId, schedule, seatCode);
//         return reservationRepository.save(reservation);
//     }

//     // 예매 ID로 예매 정보를 조회하는 메서드 (취소 시 필요)
//     public Reservation getReservationById(Long reservationId) {
//         return reservationRepository.findById(reservationId)
//                 .orElseThrow(() -> new EntityNotFoundException("해당 예매를 찾을 수 없습니다. ID: " + reservationId));
//     }

//     // 예매 정보를 삭제하는 메서드 (취소 시 필요)
//     @Transactional
//     public void deleteReservation(Long reservationId) {
//         reservationRepository.deleteById(reservationId);
//     }
// }