package com.ssafy.ttocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssafy.ttocket.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "공연 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PerformanceDto {
    @Schema(description = "공연 ID")
    @JsonProperty("performance_id")
    private int id;
    @Schema(description = "유저 ID")
    @JsonProperty("user_id")
    private String userId;
    @Schema(description = "공연명")
    private String title;
    @Schema(description = "티케팅 시작 시간")
    @JsonProperty("start_time")
    private String startTime;
    @Schema(description = "티케팅 마감 시간 = 공연 시작 시간")
    @JsonProperty("end_time")
    private String endTime;
    @Schema(description = "공연 장소")
    private String location;
    @Schema(description = "티켓 가격")
    private double price;
    @Schema(description = "공연 최대 좌석 수")
    @JsonProperty("max_seats")
    private int maxSeats;
    @Schema(description = "공연 소개")
    private String desc;
    @Schema(description = "공연 기타 정보")
    private String etc;
    @Schema(description = "공연 포스터 url")
    private String poster;

}
