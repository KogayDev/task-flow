package com.kogay.taskflow.unit.http.rest;

import com.kogay.taskflow.dto.LoginDto;
import com.kogay.taskflow.dto.RegisterDto;
import com.kogay.taskflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.kogay.taskflow.util.JsonConverter.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class AuthControllerTest {

    private static final String USERNAME = "ivan.loker@mail.ru";
    private static final String PASSWORD = "111";
    private static final String FIRST_NAME = "Ivan";
    private static final String LAST_NAME = "Loker";
    private static final LocalDate BIRTH_DATE = LocalDate.now();

    private final MockMvc mockMvc;

    @MockBean
    private final AuthService authService;

    @Test
    void login() throws Exception {
        // Given
        String jwtToken = "token";
        LoginDto loginDto = new LoginDto(USERNAME, PASSWORD);

        doReturn(jwtToken)
                .when(authService)
                .login(any());

        // When
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(loginDto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(jwtToken));
    }

    @Test
    void register() throws Exception {
        // Given
        String jwtToken = "token";
        RegisterDto registerDto = new RegisterDto(USERNAME, PASSWORD, PASSWORD, FIRST_NAME, LAST_NAME, BIRTH_DATE);

        doReturn(jwtToken)
                .when(authService)
                .register(any());

        // When
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(registerDto)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(jwtToken));
    }
}