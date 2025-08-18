package com.example.demo.controller;

import com.example.demo.service.PerformanceService;
import com.example.demo.domain.Performance;
import com.example.demo.domain.Reservation;
import com.example.demo.domain.Schedule;
import com.example.demo.dto.TicketEvent;
import com.example.demo.service.KafkaProducerService;
import com.example.demo.service.PaymentService;
import com.example.demo.service.ReservationService;

import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import java.util.Optional;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    private final ReservationService reservationService;
    // ▼▼▼ --- 수정된 부분 --- ▼▼▼
    private final KafkaProducerService kafkaProducerService;

    public PaymentController(PaymentService paymentService, ReservationService reservationService,KafkaProducerService kafkaProducerService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
        this.kafkaProducerService = kafkaProducerService;  
    }
    // ▲▲▲ --- 수정된 부분 --- ▲▲▲

    @Operation(summary = "결제 확인 처리", description = "예매에 대한 결제를 처리하고 티켓 시스템에 정보를 전송합니다.")
    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<?> confirmPayment(
            @Parameter(description = "예매 ID", required = true) @PathVariable Long reservationId) {
        
        boolean paymentSuccess = paymentService.processPayment(reservationId, "결제정보");

        if (paymentSuccess) {
            // ▼▼▼ --- 수정된 부분 --- ▼▼▼
            Optional<Reservation> reservationOptional = reservationService.getReservationById(reservationId);
            if (reservationOptional.isPresent()) {
                Reservation reservation = reservationOptional.get();
                Schedule schedule = reservation.getSchedule();
                Performance performance = schedule.getPerformance();

                // 카프카로 보낼 TicketEvent DTO 생성
                TicketEvent event = TicketEvent.builder()
                        .reservation_id(reservation.getId())
                        .schedule_id(schedule.getId())
                        .user_name(reservation.getUserId()) // 예매 시 저장된 사용자 ID 사용
                        .seat_code(reservation.getSeatCode())
                        .movie_id(performance.getId())
                        .movie_name(performance.getName())
                        .movie_time(schedule.getScheduleTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .poster_url("https://image.tmdb.org/t/p/w500/interstellar_poster.jpg") // 임시 URL
                        .build();
                
                // 카프카 메시지 전송
                kafkaProducerService.sendTicketEvent(event);
            }
            // ▲▲▲ --- 수정된 부분 --- ▲▲▲
            
            return ResponseEntity.ok(new PaymentConfirmation(reservationId, "success"));
        } else {
            return ResponseEntity.status(402).body(new PaymentConfirmation(reservationId, "failure"));
        }
    }
}