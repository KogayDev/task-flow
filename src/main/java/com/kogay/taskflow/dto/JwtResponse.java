package com.kogay.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class JwtResponse {
    String token;

    @JsonCreator
    public static JwtResponse of(@JsonProperty("token") String token) {
        return new JwtResponse(token);
    }
}
