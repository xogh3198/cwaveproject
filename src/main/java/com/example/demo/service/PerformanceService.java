package com.example.demo.service;

import com.example.demo.domain.Performance;
import com.example.demo.domain.Schedule;
import com.example.demo.repository.PerformanceRepository;
import com.example.demo.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final ScheduleRepository scheduleRepository;

    public PerformanceService(PerformanceRepository performanceRepository, ScheduleRepository scheduleRepository) {
        this.performanceRepository = performanceRepository;
        this.scheduleRepository = scheduleRepository;
    }

    // 이 메서드에 @Transactional 어노테이션을 추가하여 지연 로딩 문제를 해결합니다.
    @Transactional
    public List<Performance> getAllPerformances() {
        //List<Performance> performances = performanceRepository.findAll();
        // 스케줄 정보를 강제로 로딩하여 세션 문제를 방지합니다.
        //performances.forEach(p -> p.getSchedules().size()); 
        //return performances;
        return performanceRepository.findAll();
    }

    public Optional<Performance> getPerformance(Long id) {
        return performanceRepository.findById(id);
    }

    public List<Schedule> getAvailableSchedules(Long performanceId) {
        return scheduleRepository.findByPerformanceId(performanceId);
    }

    @Transactional
    public void confirmReservation(Long scheduleId) {
        scheduleRepository.findById(scheduleId).ifPresent(schedule -> {
            if (schedule.getRemainingSeats() > 0) {
                schedule.setRemainingSeats(schedule.getRemainingSeats() - 1);
                scheduleRepository.save(schedule);
            } else {
                throw new IllegalStateException("잔여 좌석이 없습니다.");
            }
        });
    }
}