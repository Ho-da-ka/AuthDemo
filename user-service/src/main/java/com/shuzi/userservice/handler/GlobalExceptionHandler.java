package com.shuzi.userservice.handler;

import com.shuzi.userservice.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handle(RuntimeException ex) {
        return Result.error(ex.getMessage());
    }
}