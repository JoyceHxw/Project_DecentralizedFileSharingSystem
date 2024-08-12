package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class DiluteRequest {
    private Integer userId;
    private Integer accountId;
    private String batchId;
    private Long depth;
}
