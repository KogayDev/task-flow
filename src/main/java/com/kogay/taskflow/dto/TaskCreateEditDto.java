package com.kogay.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskCreateEditDto {
    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotBlank
    String status;
}
