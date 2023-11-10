package com.kogay.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@RequiredArgsConstructor
@Builder
public class UserEditDto {
    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @PastOrPresent
    LocalDate birthDate;
}
