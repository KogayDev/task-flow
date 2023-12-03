package com.kogay.taskflow.dto;

import com.kogay.taskflow.entity.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskReadDto {
    Integer id;
    String name;
    String description;
    Status status;
    UserReadDto owner;
    List<AssigneeReadDto> assignees;
}
