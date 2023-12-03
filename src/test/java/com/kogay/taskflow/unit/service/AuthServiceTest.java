package com.kogay.taskflow.unit.service;

import com.kogay.taskflow.dto.LoginDto;
import com.kogay.taskflow.dto.RegisterDto;
import com.kogay.taskflow.entity.Role;
import com.kogay.taskflow.exception.UserAlreadyExistsException;
import com.kogay.taskflow.mapper.RegisterMapper;
import com.kogay.taskflow.repository.UserRepository;
import com.kogay.taskflow.service.AuthService;
import com.kogay.taskflow.service.JwtProvider;
import com.kogay.taskflow.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@RequiredArgsConstructor
class AuthServiceTest {

    private static final String USERNAME = "test@gmail.com";
    private static final String PASSWORD = "testPassword";
    private static final String FIRST_NAME = "Ivan";
    private static final String LAST_NAME = "Ivanovich";
    private static final LocalDate BIRTH_DATE = LocalDate.now();

    private static final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            new User(USERNAME, PASSWORD, new ArrayList<>()),
            PASSWORD
    );

    @Mock
    private final UserRepository userRepository;

    @Mock
    private final AuthenticationManager authenticationManager;

    @Mock
    private final RoleService roleService;

    @SpyBean
    private final JwtProvider jwtProvider;

    @SpyBean
    private final ModelMapper modelMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnJwtToken() {
        // Given
        LoginDto loginDto = new LoginDto(USERNAME, PASSWORD);
        doReturn(authenticationToken)
                .when(authenticationManager)
                .authenticate(any());

        // When 
        String jwtToken = authService.login(loginDto);

        // Then
        assertThat(jwtToken).isNotEmpty();
    }

    @Test
    void registerReturnJwtToken() {
        // Given
        doReturn(authenticationToken)
                .when(authenticationManager)
                .authenticate(any());

        doReturn(Role.USER)
                .when(roleService)
                .getDefaultRole();

        // When 
        String register = authService.register(new RegisterDto(
                USERNAME,
                PASSWORD,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME,
                BIRTH_DATE
        ));

        // Then
        assertThat(register).isNotEmpty();
    }

    @Test
    void registerDuplicateUserThrowsUserAlreadyExistsException() {
        // Given
        RegisterDto registerDto = new RegisterDto(
                USERNAME,
                PASSWORD,
                PASSWORD,
                FIRST_NAME,
                LAST_NAME,
                BIRTH_DATE
        );

        com.kogay.taskflow.entity.User user = new com.kogay.taskflow.entity.User();
        doReturn(Optional.of(user))
                .when(userRepository)
                .findByUsername(USERNAME);

        // When
        assertThatThrownBy(() -> authService.register(registerDto))
                // Then
                .isInstanceOf(UserAlreadyExistsException.class);
    }
}