package com.kogay.taskflow.util;

import com.kogay.taskflow.dto.ErrorResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseEntityExceptionFactory {
    public ResponseEntity<Object> of(HttpStatusCode httpStatusCode, Exception exception) {
        return of(httpStatusCode, exception.getMessage());
    }

    public ResponseEntity<Object> of(HttpStatusCode httpStatusCode, String message) {
        return new ResponseEntity<>(
                ErrorResponse.of(httpStatusCode.value(), message),
                httpStatusCode
        );
    }
}
