package com.kogay.taskflow.http.rest;

import com.kogay.taskflow.dto.PageResponse;
import com.kogay.taskflow.dto.TaskCreateEditDto;
import com.kogay.taskflow.dto.TaskFilter;
import com.kogay.taskflow.dto.TaskReadDto;
import com.kogay.taskflow.entity.Status;
import com.kogay.taskflow.exception.TaskNotFoundException;
import com.kogay.taskflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/tasks")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Create task",
            description = "Create task.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task created successfully.", content = @Content(schema = @Schema(implementation = TaskCreateEditDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request content.", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Task with specified name already exists.", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody TaskCreateEditDto taskCreateEditDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(taskCreateEditDto));
    }

    @Operation(
            summary = "Find Task by ID",
            description = "Finds a Task by the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task found.", content = @Content(schema = @Schema(implementation = TaskReadDto.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found.", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Operation(
            summary = "Find All Tasks with Filter",
            description = "Retrieves a list of Tasks based on the provided filtering criteria. Query parameters are used to specify filtering conditions using QueryDSL with TaskFilter.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tasks found.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = TaskReadDto.class)))),
            }
    )
    @PageableAsQueryParam
    @GetMapping
    public ResponseEntity<?> findAll(TaskFilter taskFilter,
                                     @Parameter(hidden = true) Pageable pageable) {
        Page<TaskReadDto> page = taskService.findAll(taskFilter, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @Operation(
            summary = "Update Task",
            description = "Updates an existing Task in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task updated successfully.", content = @Content(schema = @Schema(implementation = TaskCreateEditDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request content.", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied. You are not the owner of this task or an admin.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Task not found.", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Task with specified name already exists.", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Validated @RequestBody TaskCreateEditDto taskCreateEditDto) {
        return taskService.update(id, taskCreateEditDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(
            summary = "Delete Task By ID",
            description = "Deletes a Task by the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task deleted successfully.", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied. You are not the owner of this task or an admin.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Task not found.", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return taskService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Change Task Status",
            description = "Changes the status of an existing Task.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task status changed successfully.", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied. You are not the owner of this task or an admin.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Task not found.", content = @Content)
            }
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Integer id,
                                          @RequestParam Status newStatus) {
        taskService.changeStatus(id, newStatus);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add Assignee to Task",
            description = "Assigns a user to the specified Task.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Assignee added to the task.", content = @Content(schema = @Schema(implementation = TaskReadDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request content.", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied. You are not the owner of this task or an admin.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Task or User not found.", content = @Content)
            }
    )
    @PostMapping("/{id}/assignees")
    public ResponseEntity<?> addAssigneeToTask(@PathVariable Integer id,
                                               @RequestBody Integer userId) {
        return ResponseEntity.ok(taskService.addAssigneeToTask(id, userId));
    }

    @Operation(
            summary = "Remove Assignee from Task",
            description = "Removes a user from the specified Task.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Assignee removed from the task.", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied. You are not the owner of this task or an admin.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Task or User not found.", content = @Content)
            }
    )
    @DeleteMapping("/{id}/assignees")
    public ResponseEntity<?> removeAssigneeFromTask(@PathVariable Integer id,
                                                    @RequestBody Integer userId) {
        return taskService.removeAssigneeFromTask(id, userId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
