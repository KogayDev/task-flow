package com.kogay.taskflow.unit.http.rest;

import com.kogay.taskflow.dto.UserEditDto;
import com.kogay.taskflow.dto.UserReadDto;
import com.kogay.taskflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.kogay.taskflow.util.JsonConverter.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"USER", "ADMIN"})
class UserControllerTest {

    private static final int USER_ID = 1;

    private final MockMvc mockMvc;

    @MockBean
    private final UserService userService;

    @Test
    void findById() throws Exception {
        // Given
        UserReadDto user = new UserReadDto(USER_ID, "dummy", "dummy", "dummy", LocalDate.now());
        doReturn(Optional.of(user))
                .when(userService)
                .findById(USER_ID);

        // When
        mockMvc.perform(get("/api/v1/users/" + USER_ID))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void findAll() throws Exception {
        // Given
        UserReadDto user = new UserReadDto(USER_ID, "dummy", "dummy", "dummy", LocalDate.now());

        List<UserReadDto> users = List.of(user, user, user, user, user);

        doReturn(new PageImpl<>(users))
                .when(userService)
                .findAll(any(), any());

        // When
        mockMvc.perform(get("/api/v1/users"))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.size").value(users.size()));
    }

    @Test
    void update() throws Exception {
        // Given
        UserEditDto userEditDto = UserEditDto.builder()
                .firstName("dummy")
                .lastName("dummy")
                .birthDate(LocalDate.now())
                .build();

        UserReadDto userReadDto = UserReadDto.builder()
                .firstName(userEditDto.getFirstName())
                .lastName(userEditDto.getLastName())
                .birthDate(userEditDto.getBirthDate())
                .build();

        doReturn(Optional.of(userReadDto))
                .when(userService)
                .update(USER_ID, userEditDto);

        // When
        mockMvc.perform(put("/api/v1/users/" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(userEditDto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userEditDto.getFirstName()));
    }

    @Test
    void delete() throws Exception {
        // Given
        doReturn(true)
                .when(userService)
                .delete(USER_ID);

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/" + USER_ID))
                // Then
                .andExpect(status().isNoContent());
    }
}