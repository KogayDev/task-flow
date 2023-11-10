package com.kogay.taskflow.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Integer userId) {
        super(String.format("user with id '%d' not found", userId));
    }
}
