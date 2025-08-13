package com.example.demo.controller;

import com.example.demo.service.PerformanceService;
import com.example.demo.service.PaymentService;
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

// PaymentConfirmation 객체 추가 (결제 성공 응답용)
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

    public PaymentController(PaymentService paymentService, PerformanceService performanceService) {
        this.paymentService = paymentService;
        this.performanceService = performanceService;
    }

    // 결제 완료 API
    @Operation(summary = "결제 확인 처리", description = "예매에 대한 결제를 처리하고 좌석을 확정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "결제 성공",
                    content = @Content(schema = @Schema(implementation = PaymentConfirmation.class))),
        @ApiResponse(responseCode = "402", description = "결제 실패",
                    content = @Content(schema = @Schema(implementation = PaymentConfirmation.class)))
    })
    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<PaymentConfirmation> confirmPayment(
            @Parameter(description = "예매 ID", required = true) @PathVariable Long reservationId) {
        // PaymentService를 통해 결제 로직 호출
        boolean paymentSuccess = paymentService.processPayment(reservationId, "결제정보");

        if (paymentSuccess) {
            // 결제 성공 시 좌석 확정 로직 호출
            performanceService.confirmReservation(reservationId);
            return ResponseEntity.ok(new PaymentConfirmation(reservationId, "success"));
        } else {
            return ResponseEntity.status(402).body(new PaymentConfirmation(reservationId, "failure"));
        }
    }
}