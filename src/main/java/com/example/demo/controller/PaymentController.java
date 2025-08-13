package com.example.demo.controller;

import com.example.demo.service.PerformanceService;
import com.example.demo.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.AllArgsConstructor;

// PaymentConfirmation 객체 추가 (결제 성공 응답용)
@Getter @AllArgsConstructor
class PaymentConfirmation {
    private final Long reservationId;
    private final String status;
}

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PerformanceService performanceService;

    public PaymentController(PaymentService paymentService, PerformanceService performanceService) {
        this.paymentService = paymentService;
        this.performanceService = performanceService;
    }

    // 결제 완료 API
    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<PaymentConfirmation> confirmPayment(@PathVariable Long reservationId) {
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