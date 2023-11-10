package com.kogay.taskflow.integration.service;

import com.kogay.taskflow.dto.LoginDto;
import com.kogay.taskflow.dto.RegisterDto;
import com.kogay.taskflow.integration.IntegrationTestBase;
import com.kogay.taskflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class AuthServiceIT extends IntegrationTestBase {

    private final AuthService authService;

    @Test
    void login() {
        // Given
        LoginDto loginDto = new LoginDto("ivan_ivanov@mail.ru", "123");

        // When
        String jwt = authService.login(loginDto);

        // Then
        assertThat(jwt).isNotBlank();
    }

    @Test
    void register() {
        // Given
        RegisterDto registerDto = new RegisterDto("anime@mail.ru", "111", "111", "Anime", "Animexovich", LocalDate.now());

        // When
        String jwt = authService.register(registerDto);

        // Then
        assertThat(jwt).isNotBlank();
    }
}
