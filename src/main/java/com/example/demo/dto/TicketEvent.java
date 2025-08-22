package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketEvent {
    private Long reservation_id;
    private Long schedule_id;
    private String user_name; 
    private String seat_code;
    private Long movie_id;
    private String movie_name;
    private String movie_time;
    private String poster_url; 
}