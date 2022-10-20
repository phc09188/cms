package com.zerobase.cms.user.controller;


import com.zerobase.cms.user.exception.CustomerException;
import com.zerobase.cms.user.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler({
            CustomerException.class
    })
    public ResponseEntity<ExceptionResponse> customRequestException(final CustomerException c){
        log.warn("api Exception : {}", c.getErrorCode());
        return ResponseEntity.badRequest().body(new ExceptionResponse(c.getMessage(), c.getErrorCode()));
    }

    @Getter
    @AllArgsConstructor
    public static class ExceptionResponse{
        private String message;
        private ErrorCode errorcode;
    }
}
