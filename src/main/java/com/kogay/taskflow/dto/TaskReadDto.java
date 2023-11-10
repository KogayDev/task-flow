package com.kogay.taskflow.dto;

import com.kogay.taskflow.entity.Status;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor
@Builder
public class TaskReadDto {
    Integer id;
    String name;
    String description;
    Status status;
    UserReadDto owner;
    List<AssigneeReadDto> assignees;
}
