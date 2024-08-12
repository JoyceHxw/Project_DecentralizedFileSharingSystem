package com.example.decentralized_backend.service;

import com.example.decentralized_backend.domain.request.CopyFileRequest;
import com.example.decentralized_backend.domain.request.DownloadRequest;
import com.example.decentralized_backend.domain.request.UploadRequest;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.domain.File;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 81086
* @description 针对表【file(文件表)】的数据库操作Service
* @createDate 2024-03-21 14:08:34
*/
public interface FileService extends IService<File> {

    Result upload(UploadRequest uploadRequest, HttpServletRequest request, Boolean isFile);

    Result download(DownloadRequest downloadRequest, HttpServletRequest request);

    Result find(Integer userId, String filename, Boolean isMy, HttpServletRequest request);

    Result delete(Integer userId, Integer fileId, HttpServletRequest request);

    Result findByFolder(Integer userId, Integer folderId, HttpServletRequest request);

    Result copy(CopyFileRequest copyFileRequest, HttpServletRequest request);

    void copyFile(File copiedFile, Integer targetFolderId, Integer userId, String userName);
}
