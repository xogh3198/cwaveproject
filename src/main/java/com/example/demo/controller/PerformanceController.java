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

    //private final PaymentService paymentService;
    private final PerformanceService performanceService;
    private final WaitingQueueService waitingQueueService;


    public PerformanceController(PerformanceService performanceService, WaitingQueueService waitingQueueService, PaymentService paymentService) {
        this.performanceService = performanceService;
        this.waitingQueueService = waitingQueueService;
        //this.paymentService = paymentService;
    }

    // 1. 모든 공연 목록 조회
    @Operation(summary = "전체 공연 목록 조회", description = "시스템에 등록된 모든 공연의 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = Performance.class)))
    })
    @GetMapping
    public ResponseEntity<List<Performance>> getAllPerformances() {
        return ResponseEntity.ok(performanceService.getAllPerformances());
    }

    // 2. 특정 공연 상세 정보 조회
    @Operation(summary = "공연 상세 정보 조회", description = "특정 공연의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공연 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = Performance.class))),
        @ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Performance> getPerformanceDetails(
            @Parameter(description = "공연 ID", required = true) @PathVariable Long id) {
        Performance performance = performanceService.getPerformance(id);
        if (performance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(performance);
    }
    @Operation(summary = "공연 일정 및 잔여 좌석 조회", description = "특정 공연의 일정별 잔여 좌석 수를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일정 및 좌석 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음")
    })
    @GetMapping("/{id}/schedule")
    public ResponseEntity<Map<String, Integer>> getAvailableSchedule(
            @Parameter(description = "공연 ID", required = true) @PathVariable Long id) {
        Map<String, Integer> schedule = performanceService.getAvailableSchedule(id);
        return ResponseEntity.ok(schedule);
    }
    // 3. 대기열 진입
    @Operation(summary = "대기열 진입", description = "공연 예매를 위한 대기열에 진입합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "대기열 진입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/{id}/queue")
    public ResponseEntity<String> enterWaitingQueue(
            @Parameter(description = "공연 ID", required = true) @PathVariable Long id,
            @Parameter(description = "사용자 ID", required = true) @RequestParam String userId) {
        waitingQueueService.enterQueue(id, userId);
        return ResponseEntity.ok("대기열에 성공적으로 진입했습니다.");
    }
    
    // 4. 좌석 예매 로직 호출
    @Operation(summary = "좌석 예매", description = "특정 공연의 좌석을 예매합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예매 성공",
                    content = @Content(schema = @Schema(implementation = Reservation.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (좌석 불가능, 잘못된 파라미터 등)")
    })
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Reservation> reserveSeat(
            @Parameter(description = "공연 ID", required = true) @PathVariable Long id,
            @Parameter(description = "좌석 번호", required = true) @RequestParam String seatNumber,
            @Parameter(description = "공연 일정", required = true) @RequestParam String schedule) {
        try {
            Reservation reservation = performanceService.reserveSeat(id, schedule, seatNumber);
            return ResponseEntity.ok(reservation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    }
