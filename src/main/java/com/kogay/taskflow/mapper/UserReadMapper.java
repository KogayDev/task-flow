package com.kogay.taskflow.mapper;

import com.kogay.taskflow.dto.UserReadDto;
import com.kogay.taskflow.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserReadMapper {
    UserReadDto toDto(User user);
}
