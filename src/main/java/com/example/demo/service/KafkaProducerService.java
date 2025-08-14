package com.example.demo.service;

import com.example.demo.dto.TicketEvent; // DTO 임포트
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    // ▼▼▼ --- 수정된 부분 --- ▼▼▼
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "ticket.events"; // 토픽명 변경

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTicketEvent(TicketEvent event) {
        System.out.println("Kafka 메시지 전송 (ticket.events): 예매 ID " + event.getReservation_id());
        // JSON으로 변환될 DTO 객체를 전송
        kafkaTemplate.send(TOPIC, event);
    }
    // ▲▲▲ --- 수정된 부분 --- ▲▲▲
}