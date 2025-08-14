package com.example.demo.controller;

import com.example.demo.domain.Performance;
import com.example.demo.domain.Reservation;
import com.example.demo.domain.Schedule;
import com.example.demo.service.PerformanceService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.WaitingQueueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/performances")
@Tag(name = "공연 관리", description = "공연 조회, 예매, 대기열 관리 API")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final ReservationService reservationService;
    private final WaitingQueueService waitingQueueService;


    public PerformanceController(PerformanceService performanceService, ReservationService reservationService, WaitingQueueService waitingQueueService) {
        this.performanceService = performanceService;
        this.reservationService = reservationService;
        this.waitingQueueService = waitingQueueService;
    }

    // 1. 모든 공연 목록 조회
    @Operation(summary = "전체 공연 목록 조회", description = "시스템에 등록된 모든 공연의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<Performance>> getAllPerformances() {
        List<Performance> performances = performanceService.getAllPerformances();
        return ResponseEntity.ok(performanceService.getAllPerformances());
    }

    // 2. 특정 공연 상세 정보 조회
    @Operation(summary = "공연 상세 정보 조회", description = "특정 공연의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<Performance> getPerformanceDetails(
            @Parameter(description = "공연 ID", required = true) @PathVariable Long id) {
        return performanceService.getPerformance(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "공연 일정 및 잔여 좌석 조회", description = "특정 공연의 일정별 잔여 좌석 수를 조회합니다.")
    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<Schedule>> getAvailableSchedules(
            @Parameter(description = "공연 ID", required = true) @PathVariable Long id) {
        List<Schedule> schedules = performanceService.getAvailableSchedules(id);
        return ResponseEntity.ok(schedules);
    }
    
    @PostMapping("/{scheduleId}/reserve")
    public ResponseEntity<?> reserveSeat(
            @PathVariable Long scheduleId,
            @RequestParam String seatNumber) {
        try {
            Reservation reservation = reservationService.reserveSeat(scheduleId, seatNumber);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}