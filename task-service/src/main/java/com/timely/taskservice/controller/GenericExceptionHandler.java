package com.timely.taskservice.controller;

import com.timely.taskservice.dto.ErrorDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GenericExceptionHandler {

    @ExceptionHandler({ Exception.class })
    private ResponseEntity<ErrorDto> handle(Exception e) {
        e.printStackTrace();
        ErrorDto error = new ErrorDto();
        error.setMetaInfo(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
