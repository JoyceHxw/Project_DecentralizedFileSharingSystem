package com.example.decentralized_backend.controller;

import com.example.decentralized_backend.domain.Folder;
import com.example.decentralized_backend.domain.request.CopyFolderRequest;
import com.example.decentralized_backend.domain.request.FolderCreateRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.service.FolderService;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@RestController
@RequestMapping("folder")
public class FolderController {
    @Autowired
    private FolderService folderService;

    /**
     * 创建文件夹
     * @param folderRequest 文件夹相关参数
     * @param request 请求参数
     * @return result 返回文件夹id
     */
    @PostMapping("create")
    public Result createFolder(@RequestBody FolderCreateRequest folderRequest, HttpServletRequest request){
        if(folderRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return Result.ok(folderService.createFolder(folderRequest,request),"文件夹创建成功");
    }

    /**
     * 删除文件夹
     * @param folderId 文件夹id
     * @param request 请求参数
     * @return result
     */
    @PostMapping("delete")
    public Result deleteFolder(@RequestParam Integer userId, @RequestParam Integer folderId, HttpServletRequest request){
        return folderService.deleteFolder(userId, folderId,request);
    }

    /**
     * 获取文件夹列表
     * @param userId 用户id
     * @param parentFolderId 父级目录id
     * @param request 请求参数
     * @return result
     */
    @GetMapping("find")
    public Result findFolder(@RequestParam Integer userId, @RequestParam Integer parentFolderId, HttpServletRequest request){
        return folderService.findFolder(userId, parentFolderId, request);
    }

    /**
     * 拷贝文件夹
     * @param copyFolderRequest 拷贝文件夹参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("copy")
    public Result copyFolder(@RequestBody CopyFolderRequest copyFolderRequest, HttpServletRequest request){
        if(copyFolderRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return folderService.copy(copyFolderRequest, request);
    }

}
