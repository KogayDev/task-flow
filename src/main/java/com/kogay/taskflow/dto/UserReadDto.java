package com.kogay.taskflow.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@RequiredArgsConstructor
@Builder
public class UserReadDto {
    Integer id;
    String username;
    String firstName;
    String lastName;
    LocalDate birthDate;
}

