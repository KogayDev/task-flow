package com.kogay.taskflow.exception;

import io.jsonwebtoken.JwtException;

public class TokenIsNotValidOrPresentedException extends JwtException {
    public TokenIsNotValidOrPresentedException() {
        super("Token is not valid or not provided.");
    }
}
