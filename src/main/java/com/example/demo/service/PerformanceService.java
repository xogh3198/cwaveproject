package com.example.demo.service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.example.demo.domain.Performance;
import com.example.demo.domain.Reservation;



@Service
public class PerformanceService {

    private final Map<Long, Performance> performances = new ConcurrentHashMap<>();
    private final AtomicLong reservationCounter = new AtomicLong(0);

    // 모든 공연 목록 조회
    public List<Performance> getAllPerformances() {
        return performances.values().stream().collect(Collectors.toList());
    }
    public PerformanceService() {
        performances.put(1L, new Performance(1L, "범죄도시", "범죄도시", new AtomicInteger(100)));
        performances.put(2L, new Performance(2L, "어벤져스", "어벤져스", new AtomicInteger(50)));
    }
     public List<String> getAvailableSchedule(Long performanceId) {
        if (performances.containsKey(performanceId)) {
            // 공연A와 공연B에 대한 가상의 날짜/시간 목록
            if (performanceId == 1L) {
                return Arrays.asList("2025-08-15 19:00", "2025-08-16 19:00");
            } else {
                return Arrays.asList("2025-08-20 20:00", "2025-08-21 20:00");
            }
        }
        return Collections.emptyList();
    }
    

    // 특정 공연 상세 정보 조회
    public Performance getPerformance(Long id) {
        return performances.get(id);
    }

    // 좌석 예매 로직 (동시성 제어)
    public synchronized Reservation reserveSeat(Long performanceId, String seatNumber) {
        Performance performance = performances.get(performanceId);
        if (performance == null || performance.getRemainingSeats().get() <= 0) {
            throw new IllegalArgumentException("잔여 좌석이 없습니다.");
        }
        performance.getRemainingSeats().decrementAndGet();
        
        // 잔여 좌석이 0보다 클 때만 감소
        //return new Reservation(reservationCounter.incrementAndGet());
        return new Reservation(reservationCounter.incrementAndGet(), performanceId, seatNumber, LocalDateTime.now());
}
    
    public void confirmReservation(Long performanceId) {
        Performance performance = performances.get(performanceId);
        if (performance != null) {
            performance.getRemainingSeats().decrementAndGet();
        }
    }

    // 잔여 좌석 수 조회
    public int getRemainingSeats(Long performanceId) {
        Performance performance = performances.get(performanceId);
        return performance != null ? performance.getRemainingSeats().get() : 0;
    }
}