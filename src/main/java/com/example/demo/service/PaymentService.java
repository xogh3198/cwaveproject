// src/main/java/com/example/demo/service/PaymentService.java
package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    
    // 결제 시스템 연동을 시뮬레이션하는 메서드
    public boolean processPayment(Long reservationId, String paymentInfo) {
        System.out.println("결제 시스템 호출: 예매 ID " + reservationId + "에 대한 결제 처리 중...");
        return true;
    }
}