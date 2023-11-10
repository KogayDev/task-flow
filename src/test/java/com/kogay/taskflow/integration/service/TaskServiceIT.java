package com.kogay.taskflow.integration.service;

import com.kogay.taskflow.dto.TaskCreateEditDto;
import com.kogay.taskflow.dto.TaskFilter;
import com.kogay.taskflow.dto.TaskReadDto;
import com.kogay.taskflow.entity.Status;
import com.kogay.taskflow.integration.IntegrationTestBase;
import com.kogay.taskflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
class TaskServiceIT extends IntegrationTestBase {

    private static final Integer TASK_ID = 1;
    private static final Integer USER_ID = 2;
    private static final String TASK_NAME = "Стать Java разработчиком";

    private final TaskService taskService;

    @Test
    void create() {
        // Given
        String taskName = "Задача 1";
        TaskCreateEditDto taskCreateEditDto = new TaskCreateEditDto(taskName, "Описание 1", Status.NEW.name());

        // When
        TaskReadDto taskReadDto = taskService.create(taskCreateEditDto);

        // Then
        assertThat(taskReadDto).isNotNull();
        assertThat(taskReadDto.getName()).isEqualTo(taskName);
    }

    @Test
    void findById() {
        // When
        Optional<TaskReadDto> maybeTask = taskService.findById(TASK_ID);

        // Then
        assertThat(maybeTask).isPresent();
        assertThat(maybeTask.get().getName()).isEqualTo(TASK_NAME);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, 2, null, 2", // Поиск задач с владельцем под айди 2.
            "IN_PROGRESS, null, null, 3", // Поиск задач со статусом IN_PROGRESS
            "null, null, '[1, 2]', 3", // Поиск всех задач, на которые назначены либо пользователь 1, либо пользователь 2.
            "IN_PROGRESS, 8, '[7, 8]', 1" // Задача со статусом IN_PROGRESS, владельцем 8, пользователями ответственными за выполнение либо 7, либо 8 - вернет одну задачу.
    }, nullValues = "null")
    void findAllParameterizedTest(Status status,
                                  Integer ownerId,
                                  String assigneeIdsIN,
                                  Long expectedCount) {
        // Given
        List<Integer> assigneeIds = null;
        if (assigneeIdsIN != null) {
            assigneeIds = Arrays.stream(assigneeIdsIN.replaceAll("[\\[\\]]", "").split(","))
                    .filter(s -> !s.isEmpty())
                    .map(String::trim)
                    .map(Integer::valueOf)
                    .toList();
        }

        TaskFilter taskFilter = new TaskFilter(status, ownerId, assigneeIds);
        Pageable pageable = PageRequest.of(0, 20);

        // When
        long actualElementsCount = taskService.findAll(taskFilter, pageable).getTotalElements();

        // Then
        assertThat(actualElementsCount).isEqualTo(expectedCount);
    }

    @Test
    void update() {
        // Given
        String newName = "Задача 1";
        TaskCreateEditDto taskEditDto = new TaskCreateEditDto(newName, "Описание 1", Status.NEW.name());

        // When
        Optional<TaskReadDto> updatedTask = taskService.update(TASK_ID, taskEditDto);

        // Then
        assertThat(updatedTask).isPresent();
        assertThat(updatedTask.get().getName()).isEqualTo(newName);
    }

    @Test
    void delete() {
        // When
        boolean result = taskService.delete(TASK_ID);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void changeStatus() {
        // Given
        Status newStatus = Status.COMPLETED;

        // When
        TaskReadDto taskReadDto = taskService.changeStatus(TASK_ID, newStatus);

        // Then
        assertThat(taskReadDto.getStatus()).isEqualTo(newStatus);
    }

    @Test
    void addAssigneeToTask() {
        // When
        TaskReadDto taskReadDto = taskService.addAssigneeToTask(TASK_ID, USER_ID);

        // Then
        assertThat(taskReadDto).isNotNull();
        assertThat(taskReadDto.getName()).isEqualTo(TASK_NAME);
    }

    @Test
    void removeAssigneeFromTask() {
        // Given
        Integer userId = 1;

        // When
        boolean result = taskService.removeAssigneeFromTask(TASK_ID, userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void removeAssigneeFromTaskShouldReturnFalseIfUserNotExists() {
        // Given
        Integer userId = -999;

        // When
        boolean result = taskService.removeAssigneeFromTask(TASK_ID, userId);

        // Then
        assertThat(result).isFalse();
    }
}