package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class DownloadRequest {
    private Integer userId;
    private Integer accountId;
    private String reference;
    private String path;
}
