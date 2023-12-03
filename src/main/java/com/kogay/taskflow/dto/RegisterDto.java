package com.kogay.taskflow.dto;

import com.kogay.taskflow.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
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
