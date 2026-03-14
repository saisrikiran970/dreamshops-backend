package com.projects.dreamShops.exceptions;

import com.projects.dreamShops.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex){
        String msg = "You do not have access to this action";
        return ResponseEntity.ok(new ApiResponse(msg, HttpStatus.FORBIDDEN));
    }
}
