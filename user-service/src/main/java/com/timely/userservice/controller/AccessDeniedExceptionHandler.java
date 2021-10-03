package com.timely.userservice.controller;

import com.timely.userservice.dto.ErrorDto;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessDeniedExceptionHandler {

    @ExceptionHandler({ AccessDeniedException.class })
    private ResponseEntity<ErrorDto> accessDeniedExceptionHandler(AccessDeniedException e) {
        e.printStackTrace();
        ErrorDto error = new ErrorDto();
        error.setMessage("you don't have sufficient permissions to access this resource");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
