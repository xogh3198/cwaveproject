// src/main/java/com/example/demo/service/ReservationService.java
package com.example.demo.service;

import com.example.demo.domain.Reservation;
import com.example.demo.domain.Performance;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

    private final PerformanceService performanceService;
    private final Map<Long, Reservation> reservations = new ConcurrentHashMap<>(); // 예매 내역을 저장하는 맵
    private final AtomicLong reservationIdCounter = new AtomicLong(0);

    public ReservationService(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    // public synchronized Reservation reserveSeat(Long performanceId, String seatNumber) {
    //     Performance performance = performanceService.getPerformance(performanceId);
    //     if (performance == null || performance.getRemainingSeats().get() <= 0) {
    //         throw new IllegalArgumentException("잔여 좌석이 없습니다.");
    //     }
        
    //     performance.getRemainingSeats().decrementAndGet();
        
    //     // 예매 객체를 생성하고, 맵에 저장
    //     Long newId = reservationIdCounter.incrementAndGet();
    //     Reservation reservation = new Reservation(newId, performanceId, seatNumber);
    //     reservations.put(newId, reservation);

    //     return reservation;
    // }
    public synchronized Reservation reserveSeat(Long performanceId, String schedule, String seatNumber) {
        Performance performance = performanceService.getPerformance(performanceId);
        if (performance == null) {
            throw new IllegalArgumentException("존재하지 않는 공연입니다.");
        }
        
        // PerformanceService를 통해 좌석수를 감소시킵니다.
        // 여기서 performanceService.reserveSeatBySchedule()과 같은 메서드를 사용할 수 있습니다.
        // (가정) 현재는 PerformanceService에서 직접 좌석수를 관리하는 구조이므로 아래와 같이 처리합니다.
        
        if (performance.getRemainingSeatsBySchedule().get(schedule).get() <= 0) {
            throw new IllegalArgumentException("잔여 좌석이 없습니다.");
        }
        performance.getRemainingSeatsBySchedule().get(schedule).decrementAndGet();
        
        // 예매 객체 생성 및 맵에 저장
        Long newId = reservationIdCounter.incrementAndGet();
        Reservation reservation = new Reservation(newId, performanceId, seatNumber, LocalDateTime.now());
        reservations.put(newId, reservation);

        return reservation;
    }
    
    // <<<--- 예매 ID로 예매 내역을 조회하는 메서드 --->>>
    public Optional<Reservation> getReservationById(Long id) {
        return Optional.ofNullable(reservations.get(id));
    }
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations.values());
    }
}