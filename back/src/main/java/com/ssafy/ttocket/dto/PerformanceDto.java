package com.ssafy.ttocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.ttocket.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PerformanceDto {
    private int id;
    @JsonProperty("user_id")
    private String userId;
    private String title;
    @JsonProperty("start_time")
    private String startTime;  // 공연 시작시간
    @JsonProperty("end_time")
    private String endTime;
    private String location;
    private double price;
    @JsonProperty("max_seats")
    private int maxSeats;
    private String desc;
    private String etc;
    private String poster;

}
