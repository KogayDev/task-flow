package com.kogay.taskflow.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Integer taskId) {
        super(String.format("Task with id '%d' not found", taskId));
    }
}
