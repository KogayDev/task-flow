package com.kogay.taskflow.mapper;

import com.kogay.taskflow.dto.TaskCreateEditDto;
import com.kogay.taskflow.entity.Task;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = UserReadMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface TaskCreateEditMapper {

    Task toEntity(TaskCreateEditDto taskCreateEditDto);

    Task updateEntityFromDto(TaskCreateEditDto taskCreateEditDto, @MappingTarget Task task);
}
