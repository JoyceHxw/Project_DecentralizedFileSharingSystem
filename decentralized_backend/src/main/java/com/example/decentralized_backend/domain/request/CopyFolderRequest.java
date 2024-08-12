package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class CopyFolderRequest {
    private Integer userId;
    private Integer copiedFolderId;
    private Integer targetFolderId;
}
