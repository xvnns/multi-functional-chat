package com.example.multifunctionalchat.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class BaseControllerAdvice {

    private DateTimeFormatter dateTimeFormatter;

    @ExceptionHandler(AddingToTheDatabaseException.class)
    public Object addingToTheDatabaseException(AddingToTheDatabaseException ex, WebRequest request) {
        return response(HttpStatus.FORBIDDEN, ex, request);
    }

    @ExceptionHandler(DeleteFromDatabaseException.class)
    public Object deleteFromDatabaseException(DeleteFromDatabaseException ex, WebRequest request) {
        return response(HttpStatus.FORBIDDEN, ex, request);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public Object chatNotFoundException(ChatNotFoundException ex, WebRequest request) {
        return response(HttpStatus.FORBIDDEN, ex, request);
    }

    @ExceptionHandler(RoleNotFounException.class)
    public Object roleNotFoundException(RoleNotFounException ex, WebRequest request) {
         return response(HttpStatus.FORBIDDEN, ex, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Object userNotFoundException(UserNotFoundException ex, WebRequest request) {
        return response(HttpStatus.FORBIDDEN, ex, request);
    }

    private Object response(HttpStatus status, Exception ex, WebRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", dateTimeFormatter.format(ZonedDateTime.now()));
        body.put("status", status.toString());
        body.put("message", ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(body, httpHeaders, status);
    }
}
