package com.example.demo.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaConsumerService {

    // ▼▼▼ --- 수정된 부분 --- ▼▼▼
    private final ReservationService reservationService; 

    public KafkaConsumerService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // 토픽명 변경: "reserve.events"
    @KafkaListener(topics = "reserve.events", groupId = "reserve-service-group")
    public void handleReservationCanceled(Map<String, Long> cancellationEvent) {
        Long reservationId = cancellationEvent.get("reservation_id");
        if (reservationId == null) {
            System.err.println("잘못된 취소 메시지 형식: reservation_id 누락");
            return;
        }

        System.out.println("Kafka로부터 취소 메시지 수신 (reserve.events): 예매 ID " + reservationId);

        // ReservationService의 취소 로직 호출
        reservationService.cancelReservation(reservationId);
    }
    // ▲▲▲ --- 수정된 부분 --- ▲▲▲
}