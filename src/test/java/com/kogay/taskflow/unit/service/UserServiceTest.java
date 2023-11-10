package com.kogay.taskflow.unit.service;

import com.kogay.taskflow.dto.UserEditDto;
import com.kogay.taskflow.dto.UserFilter;
import com.kogay.taskflow.dto.UserReadDto;
import com.kogay.taskflow.entity.User;
import com.kogay.taskflow.mapper.UserEditMapper;
import com.kogay.taskflow.mapper.UserReadMapper;
import com.kogay.taskflow.repository.UserRepository;
import com.kogay.taskflow.service.UserService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@RequiredArgsConstructor
class UserServiceTest {
    private static final Integer USER_ID = 1;
    private static final String FIRST_NAME = "Artyom";

    @Mock
    private final UserRepository userRepository;

    @SpyBean
    private final UserReadMapper userReadMapper;

    @SpyBean
    private final UserEditMapper userEditMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void findById() {
        // Given
        User user = User.builder()
                .firstName(FIRST_NAME)
                .build();

        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(USER_ID);

        // When
        Optional<UserReadDto> maybeUser = userService.findById(USER_ID);

        // Then
        assertThat(maybeUser).isPresent()
                .map(UserReadDto::getFirstName)
                .contains(user.getFirstName());
    }

    @Test
    void findAll() {
        // Given
        List<User> users = List.of(new User(), new User(), new User(), new User(), new User());

        doReturn(new PageImpl<>(users))
                .when(userRepository)
                .findAll(any(Predicate.class), any(Pageable.class));

        // When
        Page<UserReadDto> userPage = userService.findAll(UserFilter.empty(), Pageable.unpaged());

        // Then
        assertThat(userPage).hasSize(users.size());
    }

    @Test
    void update() {
        // Given
        User user = new User();
        doReturn(Optional.of(user))
                .when(userRepository)
                .findById(USER_ID);

        doReturn(user)
                .when(userRepository)
                .saveAndFlush(user);

        UserEditDto userEditDto = UserEditDto.builder()
                .firstName(FIRST_NAME)
                .build();

        // When
        Optional<UserReadDto> updatedUser = userService.update(USER_ID, userEditDto);

        // Then
        assertThat(updatedUser).isPresent()
                .map(UserReadDto::getFirstName)
                .contains(user.getFirstName());
    }

    @Test
    void delete() {
        // Given
        doReturn(Optional.of(new User()))
                .when(userRepository)
                .findById(USER_ID);

        // When
        boolean result = userService.delete(USER_ID);

        // Then
        assertThat(result).isTrue();
    }
}