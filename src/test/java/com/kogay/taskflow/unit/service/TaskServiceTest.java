package com.kogay.taskflow.unit.service;

import com.kogay.taskflow.dto.AssigneeReadDto;
import com.kogay.taskflow.dto.TaskCreateEditDto;
import com.kogay.taskflow.dto.TaskFilter;
import com.kogay.taskflow.dto.TaskReadDto;
import com.kogay.taskflow.entity.Status;
import com.kogay.taskflow.entity.Task;
import com.kogay.taskflow.entity.User;
import com.kogay.taskflow.exception.TaskAlreadyExistsException;
import com.kogay.taskflow.exception.TaskNotFoundException;
import com.kogay.taskflow.exception.UserAlreadyAssignedException;
import com.kogay.taskflow.exception.UserNotFoundException;
import com.kogay.taskflow.mapper.TaskCreateEditMapper;
import com.kogay.taskflow.mapper.TaskReadMapper;
import com.kogay.taskflow.repository.TaskRepository;
import com.kogay.taskflow.repository.UserRepository;
import com.kogay.taskflow.service.TaskService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@RequiredArgsConstructor
class TaskServiceTest {

    private static final Integer TASK_ID = 1;
    private static final String TASK_NAME = "test task";
    private static final Integer USER_ID = 1;

    @Mock
    private final TaskRepository taskRepository;

    @Mock
    private final UserRepository userRepository;

    @SpyBean
    private final TaskCreateEditMapper taskCreateEditMapper;

    @SpyBean
    private final TaskReadMapper taskReadMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    @WithMockUser(username = "test@gmail.com", password = "test", authorities = {"USER", "ADMIN"})
    void create() {
        // Given
        User user = new User();
        doReturn(Optional.of(user))
                .when(userRepository)
                .findByUsername(any());

        Task task = Task.builder()
                .name(TASK_NAME)
                .build();
        doReturn(task)
                .when(taskRepository)
                .save(task);

        TaskCreateEditDto newTask = TaskCreateEditDto.builder()
                .name(task.getName())
                .build();

        // When
        TaskReadDto createdTask = taskService.create(newTask);

        // Then
        assertThat(createdTask).isNotNull()
                .extracting(TaskReadDto::getName)
                .isEqualTo(task.getName());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", password = "test", authorities = {"USER", "ADMIN"})
    void createDuplicateTaskThrowsTaskAlreadyExistsException() {
        // Given
        TaskCreateEditDto taskCreateEditDto = TaskCreateEditDto.builder()
                .name(TASK_NAME)
                .build();

        doThrow(DataIntegrityViolationException.class)
                .when(taskRepository)
                .save(any());

        // When
        assertThatThrownBy(() -> taskService.create(taskCreateEditDto))
                // Then
                .isInstanceOf(TaskAlreadyExistsException.class)
                .hasMessage(String.format("Task with name '%s' already exists.", TASK_NAME));
    }

    @Test
    void findById() {
        // Given
        Task task = Task.builder()
                .name(TASK_NAME)
                .build();

        doReturn(Optional.of(task))
                .when(taskRepository)
                .findById(TASK_ID);

        // When
        Optional<TaskReadDto> maybeTask = taskService.findById(TASK_ID);

        // Then
        assertThat(maybeTask).isPresent()
                .map(TaskReadDto::getName)
                .contains(task.getName());
    }

    @Test
    void findAll() {
        // Given
        List<Task> tasks = List.of(new Task(), new Task(), new Task(), new Task(), new Task());

        doReturn(new PageImpl<>(tasks))
                .when(taskRepository)
                .findAll(any(Predicate.class), any(Pageable.class));

        // When
        Page<TaskReadDto> taskPage = taskService.findAll(TaskFilter.empty(), Pageable.unpaged());

        // Then
        assertThat(taskPage).hasSize(tasks.size());
    }

    @Test
    void update() {
        // Given
        Task task = new Task();
        doReturn(Optional.of(task))
                .when(taskRepository)
                .findById(TASK_ID);

        doReturn(task)
                .when(taskRepository)
                .saveAndFlush(task);

        TaskCreateEditDto taskDto = TaskCreateEditDto.builder()
                .name(TASK_NAME)
                .build();

        // When
        Optional<TaskReadDto> updatedTask = taskService.update(TASK_ID, taskDto);

        // Then
        assertThat(updatedTask).isPresent()
                .map(TaskReadDto::getName)
                .contains(TASK_NAME);
    }

    @Test
    void updateTaskThrowsTaskAlreadyExistsExceptionForDuplicateName() {
        // Given
        doReturn(Optional.of(new Task()))
                .when(taskRepository)
                .findById(TASK_ID);

        doThrow(DataIntegrityViolationException.class)
                .when(taskRepository)
                .saveAndFlush(any());

        TaskCreateEditDto taskCreateEditDto = TaskCreateEditDto.builder()
                .name(TASK_NAME)
                .build();

        // When
        assertThatThrownBy(() -> taskService.update(TASK_ID, taskCreateEditDto))
                // Then
                .isInstanceOf(TaskAlreadyExistsException.class)
                .hasMessage(String.format("Task with name '%s' already exists.", TASK_NAME));
    }

    @Test
    void delete() {
        // Given
        Task task = Task.builder()
                .name(TASK_NAME)
                .build();

        doReturn(Optional.of(task))
                .when(taskRepository)
                .findById(TASK_ID);

        // When
        boolean result = taskService.delete(TASK_ID);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void changeStatus() {
        // Given
        Task task = Task.builder()
                .name(TASK_NAME)
                .status(Status.IN_PROGRESS)
                .build();

        doReturn(Optional.of(task))
                .when(taskRepository)
                .findById(TASK_ID);

        doReturn(task)
                .when(taskRepository)
                .saveAndFlush(any());

        // When
        TaskReadDto updatedTask = taskService.changeStatus(TASK_ID, Status.COMPLETED);

        // Then
        assertThat(updatedTask).isNotNull()
                .extracting(TaskReadDto::getStatus)
                .isEqualTo(task.getStatus());
    }

    @Test
    void changeStatusThrowsTaskNotFoundExceptionForNonExistentTask() {
        // Given
        doReturn(Optional.empty())
                .when(taskRepository)
                .findById(TASK_ID);

        // When
        assertThatThrownBy(() -> taskService.changeStatus(TASK_ID, Status.NEW))
                // Then
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage(String.format("Task with id '%d' not found", TASK_ID));
    }

    @Test
    void addAssigneesToTask() {
        // Given
        Task task = new Task();
        doReturn(Optional.of(task))
                .when(taskRepository)
                .findById(TASK_ID);

        doReturn(task)
                .when(taskRepository)
                .save(task);

        User user = User.builder()
                .firstName("Artyom")
                .build();
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(USER_ID);

        // When
        TaskReadDto taskReadDto = taskService.addAssigneeToTask(TASK_ID, USER_ID);

        // Then
        List<AssigneeReadDto> assignees = taskReadDto.getAssignees();
        assertThat(assignees).hasSize(1);

        assertThat(assignees.get(0))
                .extracting(assignee -> assignee.getUser().getFirstName())
                .isEqualTo(user.getFirstName());
    }

    @ParameterizedTest
    @MethodSource("assigneeScenarios")
    void addAssigneesToTaskThrowsExceptionInVariousScenarios(Integer taskId,
                                                             Integer userId,
                                                             Class<? extends Exception> expectedException) {
        Task task = new Task();
        doReturn(Optional.of(task))
                .when(taskRepository)
                .findById(TASK_ID);

        User user = new User();
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(TASK_ID);

        doThrow(UserAlreadyAssignedException.class)
                .when(taskRepository)
                .save(task);

        // When
        assertThatThrownBy(() -> taskService.addAssigneeToTask(taskId, userId))
                // Then
                .isInstanceOf(expectedException);
    }

    @Test
    void removeAssigneesFromTask() {
        // Given
        Task task = new Task();
        doReturn(Optional.of(task))
                .when(taskRepository)
                .findById(TASK_ID);

        User user = new User();
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(USER_ID);

        task.addAssignee(user);

        // When
        boolean result = taskService.removeAssigneeFromTask(TASK_ID, USER_ID);

        // Then
        assertThat(result).isTrue();
    }

    private static Stream<Arguments> assigneeScenarios() {
        int invalidId = -999;
        int correctId = 1;

        return Stream.of(
                Arguments.of(invalidId, correctId, TaskNotFoundException.class),
                Arguments.of(correctId, invalidId, UserNotFoundException.class),
                Arguments.of(correctId, correctId, UserAlreadyAssignedException.class)
        );
    }
}