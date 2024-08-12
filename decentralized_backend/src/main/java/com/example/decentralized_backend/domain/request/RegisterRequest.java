package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String firstPassword;
    private String secondPassword;
}
