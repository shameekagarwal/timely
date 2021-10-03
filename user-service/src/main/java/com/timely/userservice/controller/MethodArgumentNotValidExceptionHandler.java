package com.timely.userservice.controller;

import java.util.List;

import com.timely.userservice.dto.ErrorDto;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodArgumentNotValidExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<ErrorDto> handle(MethodArgumentNotValidException e) {
        e.printStackTrace();
        ErrorDto error = new ErrorDto();
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        if (errors.size() == 0) {
            error.setMetaInfo(e.getMessage());
        } else {
            error.setMessage(errors.get(0).getDefaultMessage());
        }
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
