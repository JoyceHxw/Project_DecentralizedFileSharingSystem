package com.example.decentralized_backend.exception;

import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result businessExceptionHandler(BusinessException e){
        log.error("BusinessException"+e.getMessage(),e);
        return Result.build(null,e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException",e);
        return Result.build(null, ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
    }
}
