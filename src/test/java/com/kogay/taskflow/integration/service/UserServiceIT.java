package com.kogay.taskflow.integration.service;

import com.kogay.taskflow.dto.UserEditDto;
import com.kogay.taskflow.dto.UserFilter;
import com.kogay.taskflow.dto.UserReadDto;
import com.kogay.taskflow.integration.IntegrationTestBase;
import com.kogay.taskflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@RequiredArgsConstructor
class UserServiceIT extends IntegrationTestBase {

    private static final int USER_ID = 1;
    private static final String USERNAME = "ivan_ivanov@mail.ru";

    private final UserService userService;

    @Test
    void findById() {
        // When
        Optional<UserReadDto> maybeUser = userService.findById(USER_ID);

        // Then
        assertThat(maybeUser).isPresent();
        assertThat(maybeUser.get().getUsername()).isEqualTo("ivan_ivanov@mail.ru");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, null, 2023-10-26, 10",
            "Ivan, null, null, 2",
            "null, Smirnov, null, 2",
            "Elena, Kovaleva, 1986-08-08, 1"
    }, nullValues = "null")
    void findAllParameterizedTest(String firstname,
                                  String lastname,
                                  LocalDate birthDate,
                                  Long expectedCount) {
        // Given
        UserFilter userFilter = new UserFilter(firstname, lastname, birthDate);
        Pageable pageable = Pageable.unpaged();

        // When
        long actualElementsCount = userService.findAll(userFilter, pageable).getTotalElements();

        // Then
        assertThat(actualElementsCount).isEqualTo(expectedCount);
    }

    @Test
    void update() {
        // When
        Optional<UserReadDto> updatedUser = userService.update(USER_ID, new UserEditDto("Alexey", "Kovalev", LocalDate.now()));

        // Then
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void delete() {
        // When
        boolean result = userService.delete(USER_ID);

        // Then
        assertThat(result).isTrue();
    }
}