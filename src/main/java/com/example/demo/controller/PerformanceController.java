package com.example.demo.controller;

import com.example.demo.domain.Performance;
import com.example.demo.domain.Reservation;
import com.example.demo.service.PerformanceService;
import com.example.demo.service.WaitingQueueService;
import com.example.demo.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/performances")
public class PerformanceController {

    //private final PaymentService paymentService;
    private final PerformanceService performanceService;
    private final WaitingQueueService waitingQueueService;


    public PerformanceController(PerformanceService performanceService, WaitingQueueService waitingQueueService, PaymentService paymentService) {
        this.performanceService = performanceService;
        this.waitingQueueService = waitingQueueService;
        //this.paymentService = paymentService;
    }

    // 1. 모든 공연 목록 조회
    @GetMapping
    public ResponseEntity<List<Performance>> getAllPerformances() {
        return ResponseEntity.ok(performanceService.getAllPerformances());
    }

    // 2. 특정 공연 상세 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<Performance> getPerformanceDetails(@PathVariable Long id) {
        Performance performance = performanceService.getPerformance(id);
        if (performance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(performance);
    }
    @GetMapping("/{id}/schedule")
    // public ResponseEntity<List<String>> getAvailableSchedule(@PathVariable Long id) {
    //     // List<String> schedule = performanceService.getAvailableSchedule(id);
    //     // return ResponseEntity.ok(schedule);
    //     return ResponseEntity.ok(performanceService.getAvailableSchedule(id));
    // }
    public ResponseEntity<Map<String, Integer>> getAvailableSchedule(@PathVariable Long id) {
        Map<String, Integer> schedule = performanceService.getAvailableSchedule(id);
        return ResponseEntity.ok(schedule);
    }
    // 3. 대기열 진입
    @PostMapping("/{id}/queue")
    public ResponseEntity<String> enterWaitingQueue(@PathVariable Long id, @RequestParam String userId) {
        waitingQueueService.enterQueue(id, userId);
        return ResponseEntity.ok("대기열에 성공적으로 진입했습니다.");
    }
    
    // 4. 좌석 예매 로직 호출
    @PostMapping("/{id}/reserve")
    // public ResponseEntity<Reservation> reserveSeat(@PathVariable Long id, @RequestParam String seatNumber, @RequestParam String schedule) {
    //     try {
    //         // 좌석 예매 로직을 수행하고, 예매 ID를 가진 Reservation 객체 반환
    //         Reservation reservation = performanceService.reserveSeat(id, schedule, seatNumber);
    //         // 결제 API를 따로 호출하므로, 여기서는 예매 ID만 반환
    //         return ResponseEntity.ok(reservation); // 유효한 JSON 객체로 반환
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    // public ResponseEntity<Reservation> reserveSeat(@PathVariable Long id, 
    //                                                @RequestParam String seatNumber,@RequestParam String schedule) {
    //     try {
    //         Reservation reservation = performanceService.reserveSeat(id, seatNumber,seatNumber);
    //         return ResponseEntity.ok(reservation); // 유효한 JSON 객체로 반환
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    public ResponseEntity<Reservation> reserveSeat(@PathVariable Long id, 
                                                   @RequestParam String seatNumber,
                                                   @RequestParam String schedule) {
        try {
            Reservation reservation = performanceService.reserveSeat(id, schedule, seatNumber);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    }
