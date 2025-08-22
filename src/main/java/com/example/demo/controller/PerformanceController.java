package com.example.demo.controller;

import java.security.Principal;
import com.example.demo.domain.Performance;
import com.example.demo.domain.Reservation;
import com.example.demo.domain.Schedule;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.service.PerformanceService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.KafkaProducerService; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/performances")
@Tag(name = "공연 관리", description = "공연 조회, 예매, 대기열 관리 API")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final ReservationService reservationService;
    private final KafkaProducerService kafkaProducerService; 

    public PerformanceController(PerformanceService performanceService, ReservationService reservationService, KafkaProducerService kafkaProducerService) {
        this.performanceService = performanceService;
        this.reservationService = reservationService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping
    public ResponseEntity<List<Performance>> getAllPerformances() {
        return ResponseEntity.ok(performanceService.getAllPerformances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Performance> getPerformanceDetails(@PathVariable Long id) {
        return performanceService.getPerformance(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<Schedule>> getAvailableSchedules(@PathVariable Long id) {
        return ResponseEntity.ok(performanceService.getAvailableSchedules(id));
    }
    
    @PostMapping("/{scheduleId}/reserve")
    public ResponseEntity<?> reserveSeat(
            @PathVariable Long scheduleId,
            Principal principal,
            @RequestBody ReservationRequest request) {
        try {
            String userId = principal.getName();
            // 1. PENDING 상태의 예매를 먼저 생성
            Reservation reservation = reservationService.createPendingReservation(scheduleId, userId, request.getSeatCode());
            
            // 2. Kafka 대기열 토픽으로 메시지 전송
            kafkaProducerService.sendReservationPendingEvent(reservation, scheduleId);

            // 3. 생성된 예매 정보 반환
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}