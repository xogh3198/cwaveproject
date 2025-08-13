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

    public PerformanceService() {
        // 공연A는 두 개의 스케줄을 가집니다.
        Map<String, Integer> scheduleA = new ConcurrentHashMap<>();
        scheduleA.put("2025-08-15 19:00", 100);
        scheduleA.put("2025-08-16 19:00", 50);
        performances.put(1L, new Performance(1L, "범죄도시", "범죄도시", scheduleA));

        // 공연B도 두 개의 스케줄을 가집니다.
        Map<String, Integer> scheduleB = new ConcurrentHashMap<>();
        scheduleB.put("2025-08-20 20:00", 200);
        scheduleB.put("2025-08-21 20:00", 150);
        performances.put(2L, new Performance(2L, "어벤져스", "어벤져스", scheduleB));
    }
    // 모든 공연 목록 조회
    public List<Performance> getAllPerformances() {
        return performances.values().stream().collect(Collectors.toList());
    }

    
    // 특정 공연 상세 정보 조회
    public Performance getPerformance(Long id) {
        return performances.get(id);
    }
    
    public Map<String, Integer> getAvailableSchedule(Long performanceId) {
        Performance performance = performances.get(performanceId);
        if (performance != null) {
            return performance.getRemainingSeatsBySchedule().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
        }
        return Collections.emptyMap();
    }
     public synchronized Reservation reserveSeat(Long performanceId, String schedule, String seatNumber) {
        Performance performance = performances.get(performanceId);
        if (performance == null) {
            throw new IllegalArgumentException("존재하지 않는 공연입니다.");
        }
        
        AtomicInteger remainingSeats = performance.getRemainingSeatsBySchedule().get(schedule);
        if (remainingSeats == null || remainingSeats.get() <= 0) {
            throw new IllegalArgumentException("잔여 좌석이 없습니다.");
        }
        
        remainingSeats.decrementAndGet();
        
        Long newId = reservationCounter.incrementAndGet();
        return new Reservation(newId, performanceId, seatNumber, LocalDateTime.now());
    }

    public void confirmReservation(Long performanceId) {
        Performance performance = performances.get(performanceId);
        if (performance != null) {
            performance.getRemainingSeatsBySchedule().values().stream().findFirst().ifPresent(AtomicInteger::decrementAndGet);
        }
    }
    public int getRemainingSeats(Long performanceId) {
        Performance performance = performances.get(performanceId);
        return performance != null ? performance.getRemainingSeatsBySchedule().values().stream().mapToInt(AtomicInteger::get).sum() : 0;
    }
}