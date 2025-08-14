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
    @Schema(description = "예매 ID", example = "991")
    private Long id;

    // ▼▼▼ --- 수정된 부분 --- ▼▼▼
    @Schema(description = "사용자 ID (Cognito로부터 받은 정보)", example = "a1b2c3d4-...")
    @Column(name = "user_id", nullable = false)
    private String userId;

    // Schedule과의 관계를 명확히 하여 공연 정보에 쉽게 접근하도록 합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    @JsonIgnore // 순환 참조 방지
    private Schedule schedule;

    @Schema(description = "좌석 코드", example = "A10")
    @Column(name = "seat_code", nullable = false)
    private String seatCode;
    // ▲▲▲ --- 수정된 부분 --- ▲▲▲

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;


    // 생성자 수정
    public Reservation(String userId, Schedule schedule, String seatCode) {
        this.userId = userId;
        this.schedule = schedule;
        this.seatCode = seatCode;
        this.reservationTime = LocalDateTime.now();
    }
}