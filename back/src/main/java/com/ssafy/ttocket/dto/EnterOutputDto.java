package com.ssafy.ttocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterOutputDto {
    @JsonProperty("enter_time")
    private LocalDateTime enterTime;

    @JsonProperty("seat_num")
    private int seatNum;

    @JsonProperty("nickname")
    private String nickname;
}
