package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class CopyFileRequest {
    private Integer userId;
    private Integer copiedFileId;
    private Integer targetFolderId;
}
