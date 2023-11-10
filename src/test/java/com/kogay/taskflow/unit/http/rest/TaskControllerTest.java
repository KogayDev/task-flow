package com.kogay.taskflow.unit.http.rest;

import com.kogay.taskflow.dto.TaskCreateEditDto;
import com.kogay.taskflow.dto.TaskReadDto;
import com.kogay.taskflow.dto.UserReadDto;
import com.kogay.taskflow.entity.Status;
import com.kogay.taskflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kogay.taskflow.util.JsonConverter.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@WithMockUser(username = "test@gmail.com", password = "test", authorities = {"USER", "ADMIN"})
class TaskControllerTest {

    private static final int TASK_ID = 0;
    private static final Integer ASSIGNEE_USER_ID = 1;
    private static final UserReadDto OWNER = UserReadDto.builder()
            .firstName("Igor")
            .build();
    private static final String TASK_NAME = "task name";

    private final MockMvc mockMvc;

    @MockBean
    private final TaskService taskService;

    @Test
    void create() throws Exception {
        // Given
        TaskCreateEditDto taskCreateEditDto = TaskCreateEditDto.builder()
                .name(TASK_NAME)
                .description("dummy")
                .status(Status.NEW.name())
                .build();

        TaskReadDto taskReadDto = new TaskReadDto(TASK_ID, taskCreateEditDto.getName(), taskCreateEditDto.getDescription(), Status.valueOf(taskCreateEditDto.getStatus()), OWNER, new ArrayList<>());
        doReturn(taskReadDto)
                .when(taskService)
                .create(taskCreateEditDto);

        // When
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(taskCreateEditDto.getName()))
                .andExpect(jsonPath("$.owner.firstName").value(OWNER.getFirstName()));
    }

    @Test
    void findById() throws Exception {
        // Given
        TaskReadDto taskReadDto = TaskReadDto.builder()
                .id(TASK_ID)
                .name(TASK_NAME)
                .owner(OWNER)
                .build();

        doReturn(Optional.of(taskReadDto))
                .when(taskService)
                .findById(TASK_ID);

        // When
        mockMvc.perform(get("/api/v1/tasks/" + TASK_ID))
                // Then
                .andExpect(jsonPath("$.name").value(taskReadDto.getName()));
    }

    @Test
    void findAll() throws Exception {
        // Given
        TaskReadDto taskReadDto = TaskReadDto.builder()
                .owner(OWNER)
                .build();
        List<TaskReadDto> tasks = List.of(taskReadDto, taskReadDto, taskReadDto);

        doReturn(new PageImpl<>(tasks, Pageable.unpaged(), tasks.size()))
                .when(taskService)
                .findAll(any(), any());

        // When
        mockMvc.perform(get("/api/v1/tasks"))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.size").value(tasks.size()));
    }

    @Test
    void update() throws Exception {
        // Given
        TaskCreateEditDto taskCreateEditDto = TaskCreateEditDto.builder()
                .name(TASK_NAME)
                .description("dummy")
                .status(Status.NEW.name())
                .build();

        TaskReadDto taskReadDto = new TaskReadDto(TASK_ID, taskCreateEditDto.getName(), taskCreateEditDto.getDescription(), Status.valueOf(taskCreateEditDto.getStatus()), OWNER, new ArrayList<>());
        doReturn(Optional.of(taskReadDto))
                .when(taskService)
                .update(TASK_ID, taskCreateEditDto);

        // When
        mockMvc.perform(put("/api/v1/tasks/" + TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(taskCreateEditDto.getName()));
    }

    @Test
    void delete() throws Exception {
        // Given
        doReturn(true)
                .when(taskService)
                .delete(TASK_ID);

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/" + TASK_ID))
                // Then
                .andExpect(status().isNoContent());
    }

    @Test
    void changeStatus() throws Exception {
        // Given
        Status newStatus = Status.COMPLETED;
        TaskReadDto taskReadDto = TaskReadDto.builder()
                .id(TASK_ID)
                .status(newStatus)
                .build();

        doReturn(taskReadDto)
                .when(taskService)
                .changeStatus(TASK_ID, newStatus);

        // When
        mockMvc.perform(patch(String.format("/api/v1/tasks/%d/status", TASK_ID))
                        .param("newStatus", newStatus.name()))
                // Then
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void addAssigneesToTask() throws Exception {
        // Given
        TaskReadDto taskReadDto = TaskReadDto.builder()
                .name(TASK_NAME)
                .build();
        doReturn(taskReadDto)
                .when(taskService)
                .addAssigneeToTask(TASK_ID, ASSIGNEE_USER_ID);

        // When
        mockMvc.perform(post(String.format("/api/v1/tasks/%d/assignees", TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ASSIGNEE_USER_ID.toString()))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TASK_NAME));
    }

    @Test
    void removeAssigneesFromTask() throws Exception {
        // Given
        doReturn(true)
                .when(taskService)
                .removeAssigneeFromTask(TASK_ID, ASSIGNEE_USER_ID);

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/api/v1/tasks/%d/assignees", TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ASSIGNEE_USER_ID.toString()))
                // Then
                .andExpect(status().isNoContent());
    }
}

