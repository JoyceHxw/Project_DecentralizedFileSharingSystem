package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class StakeRequest {
    private Integer accountId;
    private Integer userId;
    private Long amount;
}
