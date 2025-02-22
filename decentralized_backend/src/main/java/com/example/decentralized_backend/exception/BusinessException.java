package com.example.decentralized_backend.exception;

import com.example.decentralized_backend.utils.ResultCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description){
        super(message);
        this.code=code;
        this.description=description;
    }

    public BusinessException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code=resultCodeEnum.getCode();
        this.description=resultCodeEnum.getDescription();
    }

    public BusinessException(ResultCodeEnum resultCodeEnum, String description){
        super(resultCodeEnum.getMessage());
        this.code=resultCodeEnum.getCode();
        this.description=description;
    }
}
