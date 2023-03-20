package com.ssafy.ttocket.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class ResponseDto<T> {

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("body")
    private T body;
}
