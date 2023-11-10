package com.kogay.taskflow.integration.http.rest;

import com.kogay.taskflow.dto.LoginDto;
import com.kogay.taskflow.dto.RegisterDto;
import com.kogay.taskflow.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.kogay.taskflow.util.JsonConverter.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class AuthControllerIT extends IntegrationTestBase {

    private static final String USERNAME = "ivan_ivanov@mail.ru";
    private static final String PASSWORD = "123";

    private final MockMvc mockMvc;

    @Test
    void loginSuccessfully() throws Exception {
        // Given
        LoginDto loginDto = new LoginDto(USERNAME, PASSWORD);

        // When
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(loginDto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "dummy, 123",
            "dummy@mail.ru, ' '"
    })
    void loginReturnBadRequestWhenInvalidInput(String username,
                                               String password) throws Exception {
        // Given
        LoginDto loginDto = new LoginDto(username, password);

        // When
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(loginDto)))
                // Then
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerSuccessfully() throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto("newEmail@mail.ru", PASSWORD, PASSWORD, "Artyom", "Kogay", LocalDate.now());

        // When
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(registerDto)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "invalidUsername, dummy, dummy, dummy, dummy, 2019-12-12",
            "dummy@mail.ru, invalidPassword, dummy, dummy, dummy, 2019-12-12",
            "dummy@mail.ru, dummy, invalidPassword, dummy, dummy, 2019-12-12",
            "dummy@mail.ru, password, diffPassword, dummy, dummy, 2019-12-12",
            "dummy@mail.ru, dummy, dummy, invalidFirstName, dummy, 2019-12-12",
            "dummy@mail.ru, dummy, dummy, dummy, invalidLastName, 2019-12-12",
            "dummy@mail.ru, dummy, dummy, dummy, dummy, 2400-12-20", // Invalid birthDate -> @PastOrPresent.
    }, nullValues = {"invalidFirstName", "invalidLastName", "invalidPassword"})
    void registerReturnBadRequestWhenInvalidInput(String username,
                                                  String password,
                                                  String confirmPassword,
                                                  String firstName,
                                                  String lastName,
                                                  String birthDate) throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto(username, password, confirmPassword, firstName, lastName, LocalDate.parse(birthDate));

        // When
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(registerDto)))
                // Then
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerReturnConflictForDuplicateUsername() throws Exception {
        // Given
        RegisterDto registerDto = new RegisterDto(USERNAME, "dummy", "dummy", "dummy", "dummy", LocalDate.now());

        // When
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(registerDto)))
                // Then
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(String.format("User with username '%s' already exists.", USERNAME)));
    }
}
