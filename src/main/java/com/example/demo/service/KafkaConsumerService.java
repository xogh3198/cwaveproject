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

    /**
     * reservation.pending 토픽을 구독하여 예매를 순차적으로 처리하는 컨슈머
     */
    @KafkaListener(topics = "reservation.pending", groupId = "reserve-service-group")
    public void consumeReservationEvent(String message) {
        try {
            String reservationIdString = message.replace("\"", "");
            Long reservationId = Long.parseLong(reservationIdString);
            //Long reservationId = Long.parseLong(message);
            System.out.println("수신된 예매 요청 (대기열): reservationId " + reservationId);
            reservationService.confirmReservation(reservationId);
        } catch (Exception e) {
            // 처리 실패 시 로그를 남기고, 해당 메시지는 DLQ(Dead Letter Queue)로 보내는 등의 후처리 로직을 추가할 수 있습니다.
            System.err.println("예매 처리 실패. Reservation ID: " + message + ", 오류: " + e.getMessage());
        }
    }

    /**
     * 기존의 예매 취소 로직을 처리하는 컨슈머
     */
    @Transactional
    @KafkaListener(topics = "reserve.events", groupId = "reserve-service-group")
    public void consumeCancellationEvent(ConsumerRecord<String, String> record) {
        System.out.println("===== 수신된 메시지 (reserve.events) =====");
        for (Header header : record.headers()) {
            System.out.printf("Key: %s, Value: %s%n",
                    header.key(),
                    new String(header.value(), StandardCharsets.UTF_8)
            );
        }
        System.out.println("==================================");

        processCancellation(record.value());
    }

    private void processCancellation(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, Map.class);
            Object reservationIdObj = data.get("reservationId");
            
            if (reservationIdObj == null) {
                reservationIdObj = data.get("reservation_id");
            }
    
            if (reservationIdObj == null) {
                System.out.println("Reservation ID를 찾을 수 없어 메시지를 건너뜁니다.");
                return;
            }
            Long reservationId = Long.parseLong(reservationIdObj.toString());
            
            reservationService.cancelReservation(reservationId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}