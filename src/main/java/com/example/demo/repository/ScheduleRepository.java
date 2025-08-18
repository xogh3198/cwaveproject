package com.example.demo.repository;

import com.example.demo.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByPerformanceId(Long performanceId);
    @Query("SELECT s FROM Schedule s JOIN FETCH s.performance WHERE s.id = :id")
    @Override
    Optional<Schedule> findById(@Param("id") Long id);
}