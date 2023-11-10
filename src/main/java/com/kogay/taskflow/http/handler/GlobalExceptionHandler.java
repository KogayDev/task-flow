package com.kogay.taskflow.http.handler;

import com.kogay.taskflow.exception.*;
import com.kogay.taskflow.util.ResponseEntityExceptionFactory;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return ResponseEntityExceptionFactory.of(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntityExceptionFactory.of(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler({TaskNotFoundException.class, UserNotFoundException.class})
    protected ResponseEntity<?> handleNotFoundException(Exception ex) {
        return ResponseEntityExceptionFactory.of(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler({TaskAlreadyExistsException.class, UserAlreadyExistsException.class, UserAlreadyAssignedException.class})
    protected ResponseEntity<?> handleAlreadyExistsException(Exception ex) {
        return ResponseEntityExceptionFactory.of(HttpStatus.CONFLICT, ex);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ProblemDetail detail = (ProblemDetail) body;
        return ResponseEntityExceptionFactory.of(statusCode, detail.getDetail());
    }
}
