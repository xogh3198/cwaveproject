package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    //카프카 임시 주석
    private final KafkaProducerService kafkaProducerService;

    // Spring이 이 생성자를 통해 KafkaProducerService 타입의 Bean을 자동으로 주입합니다.
    public PaymentService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    // 결제 시스템 연동을 시뮬레이션하는 메서드
    public boolean processPayment(Long reservationId, String paymentInfo) {
        System.out.println("결제 시스템 호출: 예매 ID " + reservationId + "에 대한 결제 처리 중...");
        return true;
    }
}