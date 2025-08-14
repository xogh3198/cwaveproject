package com.example.demo.controller;

import com.example.demo.service.PerformanceService;
import com.example.demo.service.PaymentService;
import com.example.demo.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Schema(description = "결제 확인 응답")
@Getter @AllArgsConstructor
class PaymentConfirmation {
    @Schema(description = "예매 ID", example = "1")
    private final Long reservationId;
    
    @Schema(description = "결제 상태", example = "success", allowableValues = {"success", "failure"})
    private final String status;
}

@RestController
@RequestMapping("/api/payments")
@Tag(name = "결제 관리", description = "예매 결제 처리 API")
public class PaymentController {

    private final PaymentService paymentService;
    private final PerformanceService performanceService;
    private final ReservationService reservationService;

    public PaymentController(PaymentService paymentService, PerformanceService performanceService, ReservationService reservationService) {
        this.paymentService = paymentService;
        this.performanceService = performanceService;
        this.reservationService = reservationService;
    }

    @Operation(summary = "결제 확인 처리", description = "예매에 대한 결제를 처리하고 좌석을 확정합니다.")
    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<PaymentConfirmation> confirmPayment(
            @Parameter(description = "예매 ID", required = true) @PathVariable Long reservationId) {
        
        boolean paymentSuccess = paymentService.processPayment(reservationId, "결제정보");

        if (paymentSuccess) {
            // reservation -> schedule -> id 순서로 접근하도록 수정합니다.
            reservationService.getReservationById(reservationId).ifPresent(reservation -> {
                performanceService.confirmReservation(reservation.getSchedule().getId());
            });
            return ResponseEntity.ok(new PaymentConfirmation(reservationId, "success"));
        } else {
            return ResponseEntity.status(402).body(new PaymentConfirmation(reservationId, "failure"));
        }
    }
}