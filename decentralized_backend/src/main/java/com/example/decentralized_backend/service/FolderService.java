package com.example.decentralized_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.decentralized_backend.domain.Folder;
import com.example.decentralized_backend.domain.request.CopyFolderRequest;
import com.example.decentralized_backend.domain.request.FolderCreateRequest;
import com.example.decentralized_backend.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

/**
* @author 81086
* @description 针对表【folder(文件夹)】的数据库操作Service
* @createDate 2024-04-15 15:21:20
*/
public interface FolderService extends IService<Folder> {

    Integer createFolder(FolderCreateRequest folderRequest, HttpServletRequest request);

    Result deleteFolder(Integer userId, Integer folderId, HttpServletRequest request);

    Result findFolder(Integer userId, Integer parentFolderId, HttpServletRequest request);

    Result copy(CopyFolderRequest copyFolderRequest, HttpServletRequest request);

    void copyFolderRecursion(Folder copiedFolder, Integer targetFolderId, Integer userId, String userName);
}
