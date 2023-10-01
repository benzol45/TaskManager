package com.example.taskmanager.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AllExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler
//    public ResponseEntity<String> handleException(Exception ex) {
//        return ResponseEntity.internalServerError().body(ex.getMessage());
//    }
}
