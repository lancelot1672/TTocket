package com.ssafy.tttocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WaitQueEnterDto {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("performId")
    private int performId;
}
