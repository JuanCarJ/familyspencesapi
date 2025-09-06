package com.familyspencesapi.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class LoginUserException extends RuntimeException {
    public LoginUserException(String message) {
        super(message);
    }
}