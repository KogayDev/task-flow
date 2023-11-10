package com.kogay.taskflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginDto {
    @Email
    String username;

    @NotBlank
    String password;
}
