package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class TopUpRequest {
    private Integer userId;
    private Integer accountId;
    private String batchId;
    private Long amount;
}
