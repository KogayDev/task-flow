package com.kogay.taskflow.dto;

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
public class UserEditDto {
    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @PastOrPresent
    LocalDate birthDate;
}
