package com.kogay.taskflow.mapper;

import com.kogay.taskflow.dto.RegisterDto;
import com.kogay.taskflow.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegisterMapper {

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(RegisterDto.class, User.class)
                .addMappings(m -> m.using(getEncryptedPassword()).map(RegisterDto::getPassword, User::setPassword));
    }

    private Converter<String, String> getEncryptedPassword() {
        return ctx -> Optional.ofNullable(ctx)
                .map(MappingContext::getSource)
                .map(passwordEncoder::encode)
                .orElse(null);
    }
}
