// src/main/java/com/example/demo/controller/ReservationController.java
package com.example.demo.controller;

import com.example.demo.domain.Reservation;
import com.example.demo.service.ReservationService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "예매 관리", description = "예매 조회 및 관리 API")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // <<<--- 예매 ID로 예매 내역을 조회하는 API --->>>
    @Operation(summary = "예매 상세 조회", description = "예매 ID로 특정 예매의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예매 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = Reservation.class))),
        @ApiResponse(responseCode = "404", description = "예매를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationDetails(
            @Parameter(description = "예매 ID", required = true) @PathVariable Long id) {
        // 이 로직이 올바르게 Reservation 객체를 JSON으로 반환하는지 확인
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(summary = "전체 예매 목록 조회", description = "시스템의 모든 예매 내역을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예매 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = Reservation.class)))
    })
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }
}