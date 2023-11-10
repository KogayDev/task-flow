package com.kogay.taskflow.mapper;

import com.kogay.taskflow.annotation.EncodedMapping;
import com.kogay.taskflow.dto.RegisterDto;
import com.kogay.taskflow.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = PasswordEncoderMapper.class)
public interface RegisterMapper {

    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    User toEntity(RegisterDto register);
}
