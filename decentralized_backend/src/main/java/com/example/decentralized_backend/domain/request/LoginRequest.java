package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
