package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "reservations")
@Schema(description = "예매 정보")
@Getter @Setter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "예매 ID", example = "1")
    private Long id;

    // schedule_id를 통해 Schedule 엔티티와 관계를 맺습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Schema(description = "좌석 번호", example = "A1")
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;
    

    // 생성자를 Schedule 객체를 받도록 수정합니다.
    public Reservation(Schedule schedule, String seatNumber) {
        this.schedule = schedule;
        this.seatNumber = seatNumber;
    }
}