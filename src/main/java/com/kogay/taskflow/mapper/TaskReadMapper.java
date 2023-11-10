package com.kogay.taskflow.mapper;

import com.kogay.taskflow.dto.TaskReadDto;
import com.kogay.taskflow.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = UserReadMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskReadMapper {

    public abstract TaskReadDto toDto(Task task);
}
