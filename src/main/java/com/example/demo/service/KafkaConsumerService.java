// // package com.example.demo.service;

// // import com.example.demo.domain.Reservation;
// // import com.example.demo.domain.Schedule;
// // import com.example.demo.repository.ScheduleRepository;
// // import com.fasterxml.jackson.databind.ObjectMapper;
// // import lombok.RequiredArgsConstructor;
// // import org.springframework.kafka.annotation.KafkaListener;
// // import org.springframework.stereotype.Service;
// // import org.springframework.transaction.annotation.Transactional;

// // import java.util.Map;

// // @Service
// // @RequiredArgsConstructor
// // public class KafkaConsumerService {

// //     private final ReservationService reservationService; // ReservationService 주입
// //     private final ScheduleRepository scheduleRepository; // ScheduleRepository 주입
// //     private final ObjectMapper objectMapper = new ObjectMapper();

// //     @Transactional
// //     @KafkaListener(topics = "reserve-cancel.events", groupId = "reserve-service-group")
// //     public void consume(String message) {
// //         try {
// //             // 메시지 파싱
// //             Map<String, Object> data = objectMapper.readValue(message, Map.class);
// //             Long reservationId = Long.parseLong(data.get("reservationId").toString());

// //             // 1. 취소할 예매 정보 조회
// //             Reservation reservation = reservationService.getReservationById(reservationId);
// //             Schedule schedule = reservation.getSchedule();

// //             // 2. 남은 좌석 수 증가
// //             schedule.setRemainingSeats(schedule.getRemainingSeats() + 1);
// //             scheduleRepository.save(schedule);

// //             // 3. 예매 정보 삭제
// //             reservationService.deleteReservation(reservationId);

// //             System.out.println("Consumed message and cancelled reservation: " + reservationId);

// //         } catch (Exception e) {
// //             e.printStackTrace();
// //         }
// //     }
// // }


package com.example.demo.service;

import com.example.demo.domain.Reservation;
import com.example.demo.domain.Schedule;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.ScheduleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ReservationService reservationService;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @KafkaListener(topics = "reserve-cancel.events", groupId = "reserve-service-group")
    public void consume(ConsumerRecord<String, String> record) { // 파라미터를 ConsumerRecord로 변경
        // 수신된 메시지의 헤더를 로그로 출력합니다.
        System.out.println("===== Received Message Headers =====");
        for (Header header : record.headers()) {
            System.out.printf("Key: %s, Value: %s%n",
                    header.key(),
                    new String(header.value(), StandardCharsets.UTF_8)
            );
        }
        System.out.println("==================================");

        // 헤더의 event-type에 따라 분기 처리하는 예시
        Header eventTypeHeader = record.headers().lastHeader("event-type");
        if (eventTypeHeader != null) {
            String eventType = new String(eventTypeHeader.value(), StandardCharsets.UTF_8);
            System.out.println("Received event type: " + eventType);
            // ex) if ("reserve.released".equals(eventType)) { ... }
        }

        // 메시지 본문(payload)을 처리하는 기존 로직을 호출합니다.
        processCancellation(record.value());
    }

    // 기존 메시지 처리 로직을 별도 메서드로 분리하여 재사용성을 높입니다.
    // private void processCancellation(String message) {
    //     try {
    //         Map<String, Object> data = objectMapper.readValue(message, Map.class);
    //         Long reservationId = Long.parseLong(data.get("reservationId").toString());

    //         Reservation reservation = reservationService.getReservationById(reservationId);
    //         if (reservation != null) {
    //             Schedule schedule = reservation.getSchedule();

    //             schedule.setRemainingSeats(schedule.getRemainingSeats() + 1);
    //             scheduleRepository.save(schedule);

    //             reservationService.deleteReservation(reservationId);
    //             System.out.println("Consumed message and cancelled reservation: " + reservationId);
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
    private void processCancellation(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, Map.class);
            Long reservationId = Long.parseLong(data.get("reservationId").toString());

            // ▼▼▼▼▼▼ 이 부분이 수정됩니다 ▼▼▼▼▼▼

            // 1. Repository를 통해 직접 예매 정보를 조회합니다. (예외 발생 X)
            Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);

            // 2. 예매 정보가 존재할 경우에만 취소 로직을 실행합니다.
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                Schedule schedule = reservation.getSchedule();

                schedule.setRemainingSeats(schedule.getRemainingSeats() + 1);
                scheduleRepository.save(schedule);

                // 예외를 던지지 않는 deleteById를 사용합니다.
                reservationRepository.deleteById(reservationId);

                System.out.println("Consumed message and cancelled reservation: " + reservationId);
            } else {
                // 예매 정보가 없는 경우 로그만 남기고 정상 종료
                System.out.println("Reservation not found for ID: " + reservationId + ". Message processing skipped.");
            }
            // ▲▲▲▲▲▲ 여기까지 수정 ▲▲▲▲▲▲

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}