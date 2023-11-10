package com.kogay.taskflow.exception;

public class TaskAlreadyExistsException extends RuntimeException {

    public TaskAlreadyExistsException(String name) {
        super(String.format("Task with name '%s' already exists.", name));
    }
}
