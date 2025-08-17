// package com.example.demo.service;

// import com.example.demo.dto.TicketEvent; // DTO 임포트
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;

// @Service
// public class KafkaProducerService {

//     // ▼▼▼ --- 수정된 부분 --- ▼▼▼
//     private final KafkaTemplate<String, Object> kafkaTemplate;
//     private static final String TOPIC = "ticket.events"; // 토픽명 변경

//     public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
//         this.kafkaTemplate = kafkaTemplate;
//     }

//     public void sendTicketEvent(TicketEvent event) {
//         System.out.println("Kafka 메시지 전송 (ticket.events): 예매 ID " + event.getReservation_id());
//         // JSON으로 변환될 DTO 객체를 전송
//         kafkaTemplate.send(TOPIC, event);
//     }
//     // ▲▲▲ --- 수정된 부분 --- ▲▲▲
// }


package com.example.demo.service;

import com.example.demo.dto.TicketEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "ticket.events";

    // 메서드 이름을 sendTicketEvent로 변경하여 역할을 명확히 합니다.
    public void sendTicketEvent(TicketEvent event) {
        // ProducerRecord 객체를 생성하여 메시지와 헤더를 함께 구성합니다.
        ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC, event);

        // 헤더(Headers) 객체에 필요한 정보를 추가합니다.
        Headers headers = record.headers();
        headers.add("event-type", "ticket.issued".getBytes(StandardCharsets.UTF_8));
        headers.add("event-version", "1".getBytes(StandardCharsets.UTF_8));
        headers.add("correlation-id", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        headers.add("producer", "reserve-svc".getBytes(StandardCharsets.UTF_8)); // 현재 서비스명
        headers.add("timestamp", String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
        headers.add("content-type", "application/json".getBytes(StandardCharsets.UTF_8));

        System.out.println("Kafka 메시지 전송 (ticket.events): 예매 ID " + event.getReservation_id());
        // 헤더가 포함된 ProducerRecord 객체를 전송합니다.
        kafkaTemplate.send(record);
    }
}