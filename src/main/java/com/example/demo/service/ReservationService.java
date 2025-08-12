// src/main/java/com/example/demo/service/ReservationService.java
package com.example.demo.service;

import com.example.demo.domain.Reservation;
import com.example.demo.domain.Performance;
import org.springframework.stereotype.Service;

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

    public synchronized Reservation reserveSeat(Long performanceId, String seatNumber) {
        Performance performance = performanceService.getPerformance(performanceId);
        if (performance == null || performance.getRemainingSeats().get() <= 0) {
            throw new IllegalArgumentException("잔여 좌석이 없습니다.");
        }
        
        performance.getRemainingSeats().decrementAndGet();
        
        // 예매 객체를 생성하고, 맵에 저장
        Long newId = reservationIdCounter.incrementAndGet();
        Reservation reservation = new Reservation(newId, performanceId, seatNumber);
        reservations.put(newId, reservation);

        return reservation;
    }
    
    // <<<--- 예매 ID로 예매 내역을 조회하는 메서드 --->>>
    public Optional<Reservation> getReservationById(Long id) {
        return Optional.ofNullable(reservations.get(id));
    }
}