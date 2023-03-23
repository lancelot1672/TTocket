package com.ssafy.ttocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserlikeDto {
    private String id;
    private String title;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    private String location;
    private int price;
    @JsonProperty("max_seats")
    private int maxSeats;
    private String description;
    private String etc;

}
