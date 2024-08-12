package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class UploadRequest {
    private Integer userId;
    private Integer accountId;
    private String filename;
    private String filepath;
    private String batchId;
    private Boolean isEncrypted;
    private Integer folderId;
}
