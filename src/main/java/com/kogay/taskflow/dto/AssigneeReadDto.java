package com.kogay.taskflow.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssigneeReadDto {
    UserReadDto user;
    LocalDateTime assignmentDate;
}
