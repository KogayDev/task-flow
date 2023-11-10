package com.kogay.taskflow.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class AssigneeReadDto {
    UserReadDto user;
    LocalDateTime assignmentDate;
}
