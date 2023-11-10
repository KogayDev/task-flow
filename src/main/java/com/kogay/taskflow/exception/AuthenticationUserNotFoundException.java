package com.kogay.taskflow.exception;


import org.springframework.security.core.AuthenticationException;

public class AuthenticationUserNotFoundException extends AuthenticationException {
    public AuthenticationUserNotFoundException(String username) {
        super(String.format("User with username '%s' was not found or has been deactivated.", username));
    }
}

