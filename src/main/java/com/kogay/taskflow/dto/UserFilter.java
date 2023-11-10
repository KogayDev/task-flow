package com.kogay.taskflow.dto;

import org.springdoc.core.annotations.ParameterObject;

import java.time.LocalDate;

@ParameterObject
public record UserFilter(String firstName,
                         String lastName,
                         LocalDate birthDate) {
    public static UserFilter empty() {
        return new UserFilter(null, null, null);
    }
}
