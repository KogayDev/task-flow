package com.kogay.taskflow.exception;

public class UserAlreadyAssignedException extends RuntimeException {

    public UserAlreadyAssignedException() {
        super("User is already assigned to this task.");
    }
}
