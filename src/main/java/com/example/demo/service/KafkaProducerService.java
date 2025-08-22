// package com.example.demo.service;

// import com.example.demo.dto.TicketEvent;
// import lombok.RequiredArgsConstructor;
// import org.apache.kafka.clients.producer.ProducerRecord;
// import org.apache.kafka.common.header.Headers;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;

// import java.nio.charset.StandardCharsets;
// import java.util.UUID;

// @Service
// @RequiredArgsConstructor
// public class KafkaProducerService {

//     private final KafkaTemplate<String, Object> kafkaTemplate;
//     private static final String TOPIC = "ticket.events";
//     private static final String RESERVATION_TOPIC = "reservation.pending";

//     public void sendReservationPendingEvent(Reservation reservation, Long scheduleId) {
//         System.out.println("Kafka 메시지 전송 (reservation.pending): 예매 ID " + reservation.getId() + " | 스케줄 ID (Key): " + scheduleId);
//         // scheduleId를 메시지 키로 사용하여 파티셔닝
//         kafkaTemplate.send(RESERVATION_TOPIC, scheduleId.toString(), reservation.getId().toString());
//     }

//     // 메서드 이름을 sendTicketEvent로 변경하여 역할을 명확히 합니다.
//     public void sendTicketEvent(TicketEvent event) {
//         // ProducerRecord 객체를 생성하여 메시지와 헤더를 함께 구성합니다.
//         ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC, event);

//         // 헤더(Headers) 객체에 필요한 정보를 추가합니다.
//         Headers headers = record.headers();
//         headers.add("event-type", "ticket.issued".getBytes(StandardCharsets.UTF_8));
//         headers.add("event-version", "1".getBytes(StandardCharsets.UTF_8));
//         headers.add("correlation-id", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
//         headers.add("producer", "reserve-svc".getBytes(StandardCharsets.UTF_8)); // 현재 서비스명
//         headers.add("timestamp", String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
//         headers.add("content-type", "application/json".getBytes(StandardCharsets.UTF_8));

//         System.out.println("Kafka 메시지 전송 (ticket.events): 예매 ID " + event.getReservation_id());
//         // 헤더가 포함된 ProducerRecord 객체를 전송합니다.
//         kafkaTemplate.send(record);
//     }
// }

package com.example.demo.service;

import com.example.demo.domain.Reservation;
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
    private static final String TICKET_TOPIC = "ticket.events";
    private static final String RESERVATION_TOPIC = "reservation.pending";

    /**
     * 예매 대기열에 요청을 추가하는 메서드
     * @param reservation PENDING 상태로 생성된 예매 객체
     * @param scheduleId 메시지 키로 사용하여 동일한 스케줄은 동일 파티션으로 보내도록 함
     */
    public void sendReservationPendingEvent(Reservation reservation, Long scheduleId) {
        System.out.println("Kafka 메시지 전송 (reservation.pending): 예매 ID " + reservation.getId() + " | 스케줄 ID (Key): " + scheduleId);
        kafkaTemplate.send(RESERVATION_TOPIC, scheduleId.toString(), reservation.getId().toString());
    }

    public void sendTicketEvent(TicketEvent event) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(TICKET_TOPIC, event);

        Headers headers = record.headers();
        headers.add("event-type", "ticket.issued".getBytes(StandardCharsets.UTF_8));
        headers.add("event-version", "1".getBytes(StandardCharsets.UTF_8));
        headers.add("correlation-id", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        headers.add("producer", "reserve-svc".getBytes(StandardCharsets.UTF_8));
        headers.add("timestamp", String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
        headers.add("content-type", "application/json".getBytes(StandardCharsets.UTF_8));

        System.out.println("Kafka 메시지 전송 (ticket.events): 예매 ID " + event.getReservation_id());
        kafkaTemplate.send(record);
    }
}
