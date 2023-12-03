package com.kogay.taskflow.integration.http.rest;

import com.kogay.taskflow.dto.CustomUserDetails;
import com.kogay.taskflow.dto.UserEditDto;
import com.kogay.taskflow.entity.Role;
import com.kogay.taskflow.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static com.kogay.taskflow.util.JsonConverter.toJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor
class UserControllerIT extends IntegrationTestBase {

    private static final int USER_ID = 1;

    private final MockMvc mockMvc;

    @Test
    void findByIdSuccessfully() throws Exception {
        // When
        mockMvc.perform(get("/api/v1/users/" + USER_ID))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ivan_ivanov@mail.ru"));
    }

    @Test
    void findByIdReturnNotFoundForNonExistentUserId() throws Exception {
        // Given
        int userId = -999;

        // When
        mockMvc.perform(get("/api/v1/users/" + userId))
                // Then
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, null, null, 10",
            "Ivan, null, null, 2",
            "null, Smirnov, null, 2",
            "Elena, Kovaleva, 1986-08-08, 1"
    }, nullValues = "null")
    void findAllSuccessfully(String firstName,
                             String lastName,
                             String birthDate,
                             Integer expectedCount) throws Exception {
        // When
        mockMvc.perform(get("/api/v1/users")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .param("birthDate", birthDate))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.totalElements").value(expectedCount));
    }

    @Test
    void updateSuccessfully() throws Exception {
        // Given
        UserEditDto userEditDto =
                new UserEditDto("dummy", "dummy", LocalDate.of(1999, 9, 9));

        // When
        mockMvc.perform(put("/api/v1/users/" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(userEditDto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userEditDto.getFirstName()));
    }

    @Test
    void updateReturnForbiddenForNonOwnerOrNonAdmin() throws Exception {
        // Given
        UserEditDto userEditDto =
                new UserEditDto("dummy", "dummy", LocalDate.of(1999, 9, 9));

        // When
        mockMvc.perform(put("/api/v1/users/" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(userEditDto))
                        .with(user(new CustomUserDetails(-999, "test@gmail.com", "111", Collections.singleton(Role.USER)))))
                // Then
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, null, Loker, 1999-09-09",
            "1, Artyom, null, 1999-09-09",
            "1, null, null, 2220-12-12",
            "-999, zzz, zzz, 2015-12-12"
    }, nullValues = "null")
    void updateReturn4xxClientErrorForInvalidData(Integer userId,
                                                  String firstName,
                                                  String lastName,
                                                  String birthDate) throws Exception {
        // Given
        UserEditDto userEditDto = new UserEditDto(firstName, lastName, LocalDate.parse(birthDate));

        // When
        mockMvc.perform(put("/api/v1/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(userEditDto)))
                // Then
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteSuccessfully() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/" + USER_ID))
                // Then
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturnForbiddenForNonAdmin() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/" + USER_ID)
                        .with(user(new CustomUserDetails(USER_ID, "test@gmail.com", "111", Collections.singleton(Role.USER)))))
                // Then
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteReturnNotFoundForNonExistentUserId() throws Exception {
        // Given
        int userId = -999;

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/" + userId))
                // Then
                .andExpect(status().isNotFound());
    }
}