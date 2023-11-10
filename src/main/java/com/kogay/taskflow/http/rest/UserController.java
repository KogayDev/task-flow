package com.kogay.taskflow.http.rest;

import com.kogay.taskflow.dto.*;
import com.kogay.taskflow.exception.UserNotFoundException;
import com.kogay.taskflow.service.UserService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Find User by ID",
            description = "Finds a User by the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found.", content = @Content(schema = @Schema(implementation = UserReadDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found.", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Operation(
            summary = "Find All Users with Filter",
            description = "Retrieves a list of Users based on the provided filtering criteria. Query parameters are used to specify filtering conditions using QueryDSL with UserFilter.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users found.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserReadDto.class)))),
            }
    )
    @PageableAsQueryParam
    @GetMapping
    public ResponseEntity<?> findAll(UserFilter userFilter,
                                     @Parameter(hidden = true) Pageable pageable) {
        if (userFilter == null) {
            userFilter = UserFilter.empty();
        }

        Page<UserReadDto> page = userService.findAll(userFilter, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @Operation(
            summary = "Update User",
            description = "Updates an existing User in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully.", content = @Content(schema = @Schema(implementation = TaskCreateEditDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request content.", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied. You are not the owner of this user or an admin.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found.", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @Validated @RequestBody UserEditDto userEditDto) {
        return userService.update(id, userEditDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Operation(
            summary = "Delete User By ID",
            description = "Deletes a User by the specified ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully.", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Access denied. You are not admin.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found.", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return userService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
