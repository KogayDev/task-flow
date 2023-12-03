package com.kogay.taskflow.service;

import com.kogay.taskflow.dto.LoginDto;
import com.kogay.taskflow.dto.RegisterDto;
import com.kogay.taskflow.entity.User;
import com.kogay.taskflow.exception.UserAlreadyExistsException;
import com.kogay.taskflow.mapper.RegisterMapper;
import com.kogay.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RegisterMapper registerMapper;
    private final RoleService roleService;
    private final UserRepository userRepository;

    public String login(LoginDto loginDto) {
        Authentication authenticate = authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword())
        );

        return jwtProvider.generate((UserDetails) authenticate.getPrincipal());
    }

    @Transactional
    public String register(RegisterDto registerDto) {
        Optional<User> alreadyExistsUser = userRepository.findByUsername(registerDto.getUsername());
        if (alreadyExistsUser.isPresent()) {
            throw new UserAlreadyExistsException(registerDto.getUsername());
        }

        User user = registerMapper.toEntity(registerDto);
        user.setRole(roleService.getDefaultRole());

        userRepository.save(user);

        return jwtProvider.generate(new org.springframework.security.core.userdetails.User(
                registerDto.getUsername(),
                registerDto.getPassword(),
                Collections.singleton(user.getRole())
        ));
    }

    private Authentication authenticate(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}
