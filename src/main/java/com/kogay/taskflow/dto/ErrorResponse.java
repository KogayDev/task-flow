package com.kogay.taskflow.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class ErrorResponse {
    int status;
    String message;
}
