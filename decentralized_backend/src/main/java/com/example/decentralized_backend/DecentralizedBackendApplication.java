package com.example.decentralized_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.example.decentralized_backend.mapper")
@SpringBootApplication
public class DecentralizedBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DecentralizedBackendApplication.class, args);
    }
}
