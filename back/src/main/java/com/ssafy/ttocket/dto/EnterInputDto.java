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
public class EnterInputDto {
    @JsonProperty("performId")
    private int performId;

    @JsonProperty("seatNum")
    private int seatNum;

    @JsonProperty("nickname")
    private String nickname;
}
