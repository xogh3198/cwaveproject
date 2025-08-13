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

@Entity
@Table(name = "reservations")
@Getter @Setter
@NoArgsConstructor // <<<--- 기본 생성자 추가
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long performanceId;
    private String seatNumber;
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