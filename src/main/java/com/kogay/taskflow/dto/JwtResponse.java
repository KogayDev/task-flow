package com.kogay.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtResponse {
    String token;

    @JsonCreator
    public static JwtResponse of(@JsonProperty("token") String token) {
        return new JwtResponse(token);
    }
}
