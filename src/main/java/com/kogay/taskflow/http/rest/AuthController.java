package com.kogay.taskflow.http.rest;

import com.kogay.taskflow.dto.JwtResponse;
import com.kogay.taskflow.dto.LoginDto;
import com.kogay.taskflow.dto.RegisterDto;
import com.kogay.taskflow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "User Login",
            description = "Authenticates a user by their credentials and returns an authentication token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged in", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request content.", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseEntity.ok(JwtResponse.of(token));
    }

    @Operation(
            summary = "User Registration",
            description = "Registers a new user and returns an authentication token.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registered", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request content.", content = @Content),
                    @ApiResponse(responseCode = "409", description = "User already exists.", content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody RegisterDto registerDto) {
        String token = authService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JwtResponse.of(token));
    }
}