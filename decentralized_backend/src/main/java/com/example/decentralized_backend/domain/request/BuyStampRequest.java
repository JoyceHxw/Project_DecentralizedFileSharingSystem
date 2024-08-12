package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class BuyStampRequest {
    private Integer userId;
    private Integer accountId;
    private Long amount;
    private Integer depth;
}
