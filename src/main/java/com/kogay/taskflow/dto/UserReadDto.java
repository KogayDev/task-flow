package com.kogay.taskflow.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserReadDto {
    Integer id;
    String username;
    String firstName;
    String lastName;
    LocalDate birthDate;
}

