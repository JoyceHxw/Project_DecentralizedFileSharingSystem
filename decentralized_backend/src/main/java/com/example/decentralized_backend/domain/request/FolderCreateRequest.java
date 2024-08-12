package com.example.decentralized_backend.domain.request;

import lombok.Data;

@Data
public class FolderCreateRequest {
    private String folderName;
    private Integer parentFolderId;
    private Integer userId;
}
