package com.example.decentralized_backend.utils;

import lombok.Data;

import java.lang.reflect.Member;

/**
 * 统一返回结果
 * @param <T>
 */
@Data
public class Result<T> {
    //状态码
    private Integer code;
    //返回消息
    private String message;
    //返回错误提示
    private String description;
    //返回数据
    private T data;

    public static <T> Result<T> build(T data){
        Result<T> result=new Result<>();
        if(data!=null){
            result.setData(data);
        }
        return result;
    }

    public static <T> Result<T> build(T data, Integer code, String message, String description){
        Result<T> result=build(data);
        result.setCode(code);
        result.setMessage(message);
        result.setDescription(description);
        return result;
    }

    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum){
        Result<T> result=build(data);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }
    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum, String description){
        Result<T> result=build(data);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        result.setDescription(description);
        return result;
    }

    public static <T> Result<T> ok(T data){
        return build(data,ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> ok(T data, String description){
        return build(data,ResultCodeEnum.SUCCESS,description);
    }
}
