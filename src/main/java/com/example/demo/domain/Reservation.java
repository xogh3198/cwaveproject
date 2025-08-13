package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "reservations")
@Schema(description = "예매 정보")
@Getter @Setter
@NoArgsConstructor // <<<--- 기본 생성자 추가
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "예매 ID", example = "1")
    private Long id;
    
    @Schema(description = "공연 ID", example = "1")
    private Long performanceId;
    
    @Schema(description = "좌석 번호", example = "A1")
    private String seatNumber;
    
    @Schema(description = "예매 시간", example = "2024-12-15T14:30:00")
    private LocalDateTime reservationTime;

    public Reservation(Long id,Long performanceId, String seatNumber) {
        this.id = id;
        this.performanceId = performanceId;
        this.seatNumber = seatNumber;
        this.reservationTime = LocalDateTime.now();
    }
    public Reservation(Long id, Long performanceId, String seatNumber, LocalDateTime reservationTime) {
        this.id = id;
        this.performanceId = performanceId;
        this.seatNumber = seatNumber;
        this.reservationTime = reservationTime;
    }
}