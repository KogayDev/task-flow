package com.kogay.taskflow.integration.http.rest;

import com.kogay.taskflow.dto.TaskCreateEditDto;
import com.kogay.taskflow.entity.Status;
import com.kogay.taskflow.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.kogay.taskflow.util.JsonConverter.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor
class TaskControllerIT extends IntegrationTestBase {

    private static final String TASK_NAME = "Стать Java разработчиком";
    private static final int TASK_ID = 1;

    private final MockMvc mockMvc;

    @Test
    void createSuccessfully() throws Exception {
        // Given
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto("Task #1", "-", Status.NEW.name());

        // When
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(jsonPath("$.name").value(taskCreateEditDto.getName()))
                .andExpect(status().isCreated());
    }

    @Test
    void createReturnConflictForDuplicateTaskName() throws Exception {
        // Given
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto(TASK_NAME, "-", Status.NEW.name());

        // When
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, dummy, dummy",
            "dummy, null, dummy",
            "dummy, dummy, null",
    }, nullValues = "null")
    void createReturnBadRequestWhenInvalidInput(String name,
                                                String description,
                                                String status) throws Exception {
        // Given
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto(name, description, status);

        // When
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByIdSuccessfully() throws Exception {
        // When
        mockMvc.perform(get("/api/v1/tasks/" + TASK_ID))
                // Then
                .andExpect(jsonPath("$.name").value(TASK_NAME))
                .andExpect(status().isOk());
    }

    @Test
    void findByIdReturnNotFoundForNonExistentTaskId() throws Exception {
        // Given
        int taskId = -999;

        // When
        mockMvc.perform(get("/api/v1/tasks/" + taskId))
                // Then
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, 2, null, 2",
            "IN_PROGRESS, null, null, 3",
            "null, null, '[1, 2]', 3",
            "IN_PROGRESS, 8, '[7, 8]', 1"
    }, nullValues = "null")
    void findAllSuccessfully(String status,
                             String ownerId,
                             String assigneeIdsIN,
                             Long expectedCount) throws Exception {
        // Given
        assigneeIdsIN = assigneeIdsIN != null
                ? assigneeIdsIN.replaceAll("[\\[\\]]", "")
                : null;


        // When
        mockMvc.perform(get("/api/v1/tasks")
                        .param("status", status)
                        .param("ownerId", ownerId)
                        .param("assigneeIds", assigneeIdsIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.totalElements").value(expectedCount));
    }

    @Test
    void updateSuccessfully() throws Exception {
        // Given
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto("newTask", "-", Status.COMPLETED.name());

        // When
        mockMvc.perform(put("/api/v1/tasks/" + TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(jsonPath("$.name").value(taskCreateEditDto.getName()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateReturnForbiddenForNonOwnerOrNonAdmin() throws Exception {
        // Given
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto("newTask", "-", Status.COMPLETED.name());

        // When
        mockMvc.perform(put("/api/v1/tasks/" + TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void updateReturnNotFoundForNonExistentTaskId() throws Exception {
        // Given
        int taskId = -999;
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto("newTask", "-", Status.COMPLETED.name());

        // When
        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReturnConflictForDuplicateTaskName() throws Exception {
        // Given
        int taskId = 2;
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto(TASK_NAME, "-", Status.NEW.name());

        // When
        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, null, dummy, dummy",
            "1, dummy, null, dummy",
            "1, dummy, dummy, null"
    }, nullValues = "null")
    void updateReturnBadRequestWhenInvalidInput(Integer taskId,
                                                String name,
                                                String description,
                                                String status) throws Exception {
        // Given
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto(name, description, status);

        // When
        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(taskCreateEditDto)))
                // Then
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSuccessfully() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/" + TASK_ID))
                // Then
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteReturnForbiddenForNonOwnerOrNonAdmin() throws Exception {
        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/" + TASK_ID))
                // Then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void deleteForNonExistentTaskId() throws Exception {
        // Given
        int taskId = -999;

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/" + taskId))
                // Then
                .andExpect(status().isNotFound());
    }

    @Test
    void changeStatusSuccessfully() throws Exception {
        // Given
        String newStatus = Status.COMPLETED.name();

        // When
        mockMvc.perform(patch("/api/v1/tasks/%d/status".formatted(TASK_ID))
                        .param("newStatus", newStatus))
                // Then
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void changeStatusReturnForbiddenForNonOwnerOrNonAdmin() throws Exception {
        // Given
        String newStatus = Status.COMPLETED.name();

        // When
        mockMvc.perform(patch("/api/v1/tasks/%d/status".formatted(TASK_ID))
                        .param("newStatus", newStatus))
                // Then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void changeStatusForNonExistentTaskId() throws Exception {
        // Given
        Integer taskId = -999;
        String newStatus = Status.COMPLETED.name();

        // When
        mockMvc.perform(patch("/api/v1/tasks/%d/status".formatted(taskId))
                        .param("newStatus", newStatus))
                // Then
                .andExpect(status().isNotFound());
    }

    @Test
    void addAssigneeToTaskSuccessfully() throws Exception {
        // Given
        String userId = "9";

        // When
        mockMvc.perform(post("/api/v1/tasks/%d/assignees".formatted(TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
                // Then
                .andExpect(jsonPath("$.id").value(TASK_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void addAssigneeToTaskReturnForbiddenForNonOwnerOrNonAdmin() throws Exception {
        // Given
        String userId = "9";

        // When
        mockMvc.perform(post("/api/v1/tasks/%d/assignees".formatted(TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
                // Then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void addAssigneeToTaskReturnConflictWhenUserAlreadyAssigned() throws Exception {
        // Given
        String userId = "1";

        // When
        mockMvc.perform(post("/api/v1/tasks/%d/assignees".formatted(TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
                // Then
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-999",
            "null"
    }, nullValues = "null")
    void addAssigneeToTaskReturn4xxClientErrorWhenInvalidInput(String userId) throws Exception {
        // Given
        userId = StringUtils.nullSafeToString(userId);

        // When
        mockMvc.perform(post("/api/v1/tasks/%d/assignees".formatted(TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
                // Then
                .andExpect(status().is4xxClientError());
    }

    @Test
    void removeAssigneeFromTaskSuccessfully() throws Exception {
        // Given
        String userId = "1";

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/%d/assignees".formatted(TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
                // Then
                .andExpect(status().isNoContent());
    }
    @Test
    @WithMockUser
    void removeAssigneeFromTaskReturnForbiddenForNonOwnerOrNonAdmin() throws Exception {
        // Given
        String userId = "1";

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/%d/assignees".formatted(TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
                // Then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    void removeAssigneeFromTaskForNonExistentUserId() throws Exception {
        // Given
        String userId = "-999";

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/%d/assignees".formatted(TASK_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
                // Then
                .andExpect(status().isNotFound());
    }
}

