package com.example.decentralized_backend.utils;

import lombok.Getter;

import java.io.DataInput;

/**
 * 统一返回类型
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"success",""),
    IS_NULL(501,"null error",""),
    PARAMS_ERROR(502,"parameters error",""),
    NOT_LOGIN(500,"not login",""),
    NO_AUTH(503,"no authority",""),
    SYSTEM_ERROR(550,"system error","");

    private final Integer code;
    private final String message;
    private final String description;

    ResultCodeEnum(Integer code, String message, String description){
        this.code=code;
        this.message=message;
        this.description=description;
    }
}
