package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketEvent {
    private Long reservation_id;
    private Long schedule_id;
    private String user_name; // Cognito에서 받은 사용자 이름
    private String seat_code;
    private Long movie_id;
    private String movie_name;
    private String movie_time;
    private String poster_url; // 포스터 URL은 현재 모델에 없으므로 임시값 처리
}