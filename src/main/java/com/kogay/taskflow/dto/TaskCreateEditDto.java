package com.kogay.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder
public class TaskCreateEditDto {
    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotBlank
    String status;
}
