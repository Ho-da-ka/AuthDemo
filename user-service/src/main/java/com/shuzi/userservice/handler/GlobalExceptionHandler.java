package com.shuzi.userservice.handler;

import com.shuzi.userservice.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> handleAll(Exception ex) {
        // 遍历异常链，找到最内层的业务异常信息
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return Result.error(root.getMessage());
    }
}