package com.shuzi.userservice.handler;

import com.shuzi.userservice.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleAll(Exception ex) {
        // 遍历异常链，找到最内层的业务异常信息
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        Result<?> body = Result.error(root.getMessage());
        // 统一返回 400 Bad Request，可按需在此处做异常类型判断细分状态码
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}