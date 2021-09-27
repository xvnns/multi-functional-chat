package com.example.multifunctionalchat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class DeleteFromDatabaseException extends Exception{
    public DeleteFromDatabaseException(String message) {
        super(message);
    }
}
