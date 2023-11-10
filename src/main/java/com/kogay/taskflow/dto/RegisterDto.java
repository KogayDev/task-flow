package com.kogay.taskflow.dto;

import com.kogay.taskflow.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Value;

import java.time.LocalDate;

@Value
@PasswordMatches
public class RegisterDto {
    @Email
    @NotBlank
    String username;

    @NotBlank
    String password;

    @NotBlank
    String confirmPassword;

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @PastOrPresent
    LocalDate birthDate;
}
