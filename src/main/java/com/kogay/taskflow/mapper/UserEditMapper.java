package com.kogay.taskflow.mapper;

import com.kogay.taskflow.dto.UserEditDto;
import com.kogay.taskflow.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEditMapper {
    User updateEntityFromDto(UserEditDto userEditDto, @MappingTarget User user);
}
