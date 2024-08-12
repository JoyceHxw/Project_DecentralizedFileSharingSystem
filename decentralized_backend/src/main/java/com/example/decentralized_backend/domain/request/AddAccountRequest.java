package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class AddAccountRequest {
    private Integer userId;
    private String accountAddress;
    private String hostname;
}
