package com.example.decentralized_backend.controller;

import com.example.decentralized_backend.domain.request.CopyFileRequest;
import com.example.decentralized_backend.domain.request.DownloadRequest;
import com.example.decentralized_backend.domain.request.UploadRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.service.FileService;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@RestController
@RequestMapping("file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传单个文件
     * @param uploadRequest 文件参数
     * @param request 请求参数
     * @return request
     */
    @PostMapping("uploadFile")
    public Result uploadFile(@RequestBody UploadRequest uploadRequest, HttpServletRequest request){
        if(uploadRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return fileService.upload(uploadRequest,request,true);
    }

    /**
     * 上传文件集合，可以根据路径下载单个文件，不能下载整个
     * @param uploadRequest 文件参数
     * @param request 请求参数
     * @return request
     */
    @PostMapping("uploadDirectory")
    public Result uploadDirectory(@RequestBody UploadRequest uploadRequest, HttpServletRequest request){
        if(uploadRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return fileService.upload(uploadRequest,request,false);
    }

    /**
     * 下载文件
     * @param downloadRequest 下载相关参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("download")
    public Result download(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request){
        if(downloadRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return fileService.download(downloadRequest,request);
    }

    /**
     * 根据文件名或用户id查询所有文件
     * 需要限制，只能看到自己上传的和所在群组的文件
     * @param userId 用户Id
     * @param filename 关键词
     * @param request 请求参数
     * @return result
     */
    @GetMapping("find")
    public Result find(@RequestParam Integer userId, @RequestParam String filename, @RequestParam Boolean isMy, HttpServletRequest request){
        return fileService.find(userId, filename,isMy, request);
    }

    /**
     * 根据文件夹查找文件
     * @param userId 用户id
     * @param folderId 文件夹id
     * @param request 请求参数
     * @return result
     */
    @GetMapping("findByFolder")
    public Result findByFolder(@RequestParam Integer userId, @RequestParam Integer folderId, HttpServletRequest request){
        return fileService.findByFolder(userId, folderId, request);
    }

    /**
     * 删除文章
     * @param userId 用户id
     * @param fileId 文章id
     * @param request 请求参数
     * @return result
     */
    @PostMapping("delete")
    public Result delete(@RequestParam Integer userId, @RequestParam Integer fileId, HttpServletRequest request){
        return fileService.delete(userId,fileId,request);
    }

    /**
     * 拷贝文件
     * @param copyFileRequest 拷贝文件相关参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("copy")
    public Result copy(@RequestBody CopyFileRequest copyFileRequest, HttpServletRequest request){
        if(copyFileRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return fileService.copy(copyFileRequest,request);
    }

}
