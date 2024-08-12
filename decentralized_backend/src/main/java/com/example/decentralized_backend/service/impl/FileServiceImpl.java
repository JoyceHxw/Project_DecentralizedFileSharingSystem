package com.example.decentralized_backend.service.impl;

import com.example.decentralized_backend.domain.*;
import com.example.decentralized_backend.domain.request.CopyFileRequest;
import com.example.decentralized_backend.domain.response.FindTeamResponse;
import com.example.decentralized_backend.mapper.*;
import com.example.decentralized_backend.service.*;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.decentralized_backend.domain.request.DownloadRequest;
import com.example.decentralized_backend.domain.request.UploadRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.utils.HttpUtil;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
* @author 81086
* @description 针对表【file(文件表)】的数据库操作Service实现
* @createDate 2024-03-21 14:08:34
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService{

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private StampMapper stampMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FolderMapper folderMapper;

    //引入TeamService存在循环引用的问题
//    @Autowired
//    private TeamService teamService;

    @Autowired
    private TeamUserMapper teamUserMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Override
    public Result upload(UploadRequest uploadRequest, HttpServletRequest request, Boolean isFile) {
        String filename = uploadRequest.getFilename();
        String filepath = uploadRequest.getFilepath();
        String batchId = uploadRequest.getBatchId();
        Integer userId = uploadRequest.getUserId();
        Integer accountId = uploadRequest.getAccountId();
        Integer folderId = uploadRequest.getFolderId();
        //Step1:检验参数
        if(filename==null || batchId==null || folderId==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"文件名或邮票id或文件夹id为空");
        }
        //检验登录和账户
        Account account = accountService.verifyAccount(userId, accountId, request);
        User currentUser = userService.getCurrentUser(request);
        //检查当前文件夹存在
        LambdaQueryWrapper<Folder> queryWrapperFolder=new LambdaQueryWrapper<>();
        queryWrapperFolder.eq(Folder::getFolderId,folderId);
        Folder folder = folderMapper.selectOne(queryWrapperFolder);
        if(folder==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"文件夹不存在");
        }
        //Step2:检验邮票未过期
        LambdaQueryWrapper<Stamp> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Stamp::getBatchId,batchId);
        Stamp stamp = stampMapper.selectOne(queryWrapper);
        if(stamp==null || stamp.getIsExpired()==1){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"邮票不存在或已过期");
        }
        //Step3:发送请求
        //3.1设置请求参数
        String urlPath="http://"+ account.getHostname()+":1633/bzz?name="+filename;
        Map<String, String> headers=new HashMap<>();
        headers.put("swarm-postage-batch-id",batchId);
        //区分是单个文件还是压缩文件夹
        String contentType;
        if(isFile){
            contentType="text/plain";
        }
        else{
            contentType="application/x-tar";
            headers.put("swarm-collection", String.valueOf(true));
        }
        headers.put("Content-Type",contentType);
        //文件是否加密
        if(uploadRequest.getIsEncrypted()){
            headers.put("Swarm-Encrypt", String.valueOf(true));
        }
        //创建文件对象
        java.io.File file = new java.io.File(filepath);
        //创建文件实体，并设置文件类型
        HttpEntity entity = new FileEntity(file, ContentType.create(contentType));
        //3.2发送请求
        String dataStr = HttpUtil.httpPost(urlPath, entity, headers);
        //3.3获取文件reference
        JSONObject jsonObject = JSON.parseObject(dataStr);
        String reference=jsonObject.getString("reference");
        if(reference==null){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"上传失败");
        }
        //reference重复则不再保存
        LambdaQueryWrapper<File> queryWrapperFile=new LambdaQueryWrapper<>();
        queryWrapperFile.eq(File::getReference,reference);
        File fileFind = fileMapper.selectOne(queryWrapperFile);
        //Step4:保存到数据库
        if(fileFind==null){
            File fileSQL=new File();
            fileSQL.setFilename(filename);
            fileSQL.setFilepath(filepath);
            fileSQL.setBatchId(batchId);
            fileSQL.setReference(reference);
            fileSQL.setUserId(userId);
            fileSQL.setUserName(currentUser.getNickname()==null? currentUser.getUsername():currentUser.getNickname());
            fileSQL.setFolderId(folderId);
            if(uploadRequest.getIsEncrypted()){
                fileSQL.setIsEncrypted(1);
            }
            else{
                fileSQL.setIsEncrypted(0);
            }
            boolean save = this.save(fileSQL);
            if(!save){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据插入失败，请重新上传");
            }
        }
        return Result.ok(reference);
    }

    @Override
    public Result download(DownloadRequest downloadRequest, HttpServletRequest request) {
        Integer userId = downloadRequest.getUserId();
        Integer accountId = downloadRequest.getAccountId();
        String reference = downloadRequest.getReference();
        String path = downloadRequest.getPath();
        //Step1:检验参数
        if(reference==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"文件参数唯恐");
        }
        Account account = accountService.verifyAccount(userId, accountId, request);
        //Step2:发送请求，下载文件
        String urlPath="http://"+account.getHostname()+":1633/bzz/"+reference;
        try{
            URIBuilder uriBuilder = new URIBuilder(urlPath);
            HttpUtil.httpGetFile(uriBuilder, null, account.getHostname(),path);
        } catch(Exception e){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
        }
        return Result.ok(null,"下载成功");
    }

    @Override
    public Result find(Integer userId, String filename, Boolean isMy, HttpServletRequest request) {
        //验证登录
        userService.verifyAuth(userId,request);
        //查询数据库
        LambdaQueryWrapper<File> queryWrapper=new LambdaQueryWrapper<>();
        //区分是否需要限制仅查看当前用户上传的文件
        if(isMy){
            queryWrapper.eq(File::getUserId,userId);
        }
        if(StringUtils.isNotBlank(filename)){
            queryWrapper.like(File::getFilename,filename);
        }
        //按上传时间顺序倒序排列
        queryWrapper.orderByDesc(File::getCreateTime);
        List<File> fileList = fileMapper.selectList(queryWrapper);
        //筛选所在群组的文件，在folder中还是需要再加个teamId属性，方便查找文件
        //先找到我在的组
        HashSet<Integer> myTeamFolderSet=new HashSet<>();
        for (Team team : findMyTeam(userId)) {
            //再找每个组的文件夹
            Integer rootFolderId = team.getRootFolderId();
            getAllFolders(rootFolderId,myTeamFolderSet);
        }
        //最后再筛选
        List<File> fileListFilter=new ArrayList<>();
        for(File file: fileList){
            if(myTeamFolderSet.contains(file.getFolderId())){
                fileListFilter.add(file);
            }
        }
        return Result.ok(fileListFilter);
    }

    private void getAllFolders(Integer folderId,Set<Integer> myTeamFolderSet){
        LambdaQueryWrapper<Folder> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Folder::getParentFolderId,folderId);
        for (Folder folder : folderMapper.selectList(queryWrapper)) {
            myTeamFolderSet.add(folder.getFolderId());
            getAllFolders(folder.getFolderId(),myTeamFolderSet);
        }
    }

    private List<Team> findMyTeam(Integer userId){
        //查询数据库
        LambdaQueryWrapper<Team> queryWrapper=new LambdaQueryWrapper<>();
        Set<Integer> myTeamSet = new HashSet<>();
        LambdaQueryWrapper<TeamUser> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(TeamUser::getUserId, userId);
        for (TeamUser teamUser : teamUserMapper.selectList(queryWrapper1)) {
            myTeamSet.add(teamUser.getTeamId());
        }
        //我加入的群组，需要从关系表中找
        if(!myTeamSet.isEmpty()){
            //不能为空
            queryWrapper.in(Team::getTeamId,myTeamSet);
        }
        else{
            return new ArrayList<>();
        }
        return teamMapper.selectList(queryWrapper);
    }

    @Override
    public Result delete(Integer userId, Integer fileId, HttpServletRequest request) {
        //验证参数非空
        if(userId==null || fileId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        //验证用户权限
        userService.verifyAuth(userId,request);
        //查找文章的上传者是否一致
        LambdaQueryWrapper<File> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getFileId,fileId);
        File file = fileMapper.selectOne(queryWrapper);
        if(file==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"该文件不存在");
        }
        if(!Objects.equals(file.getUserId(), userId)){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"此没有该文件权限");
        }
        //从数据库中删除
        int i = fileMapper.deleteById(file);
        if(i==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库删除错误");
        }
        return Result.ok(i,"文章删除成功");
    }

    @Override
    public Result findByFolder(Integer userId, Integer folderId, HttpServletRequest request) {
        userService.verifyAuth(userId,request);
        if(folderId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"文件夹参数为空");
        }
        LambdaQueryWrapper<File> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getFolderId,folderId);
        //按文件名称排序
        queryWrapper.orderByAsc(File::getFilename);
        List<File> fileList = fileMapper.selectList(queryWrapper);
        return Result.ok(fileList);
    }

    @Override
    public Result copy(CopyFileRequest copyFileRequest, HttpServletRequest request) {
        Integer userId = copyFileRequest.getUserId();
        Integer copiedFileId = copyFileRequest.getCopiedFileId();
        Integer targetFolderId = copyFileRequest.getTargetFolderId();
        //验证登录
        userService.verifyAuth(userId,request);
        User currentUser = userService.getCurrentUser(request);
        String currentUserName=currentUser.getNickname()==null? currentUser.getUsername(): currentUser.getNickname();
        //参数检验
        if(copiedFileId==null || targetFolderId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"拷贝文件参数为空");
        }
        //原文件是否存在
        LambdaQueryWrapper<File> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getFileId,copiedFileId);
        File copiedFile = fileMapper.selectOne(queryWrapper);
        if(copiedFile==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"原文件不存在");
        }
        //目标文件夹是否存在
        LambdaQueryWrapper<Folder> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(Folder::getFolderId,targetFolderId);
        Folder targetFolder = folderMapper.selectOne(queryWrapper1);
        if(targetFolder==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"目标文件夹不存在");
        }
        //检查目标文件夹下是否存在同名且同reference的文件
        LambdaQueryWrapper<File> queryWrapper2=new LambdaQueryWrapper<>();
        queryWrapper2.eq(File::getFolderId,targetFolderId);
        queryWrapper2.eq(File::getReference,copiedFile.getReference());
        queryWrapper2.eq(File::getFilename,copiedFile.getFilename());
        File file1 = fileMapper.selectOne(queryWrapper2);
        if(file1!=null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"拷贝失败，存在相同文件");
        }
        copyFile(copiedFile,targetFolderId,userId,currentUserName);
        return Result.ok(null,"拷贝文件成功");
    }

    @Override
    public void copyFile(File copiedFile, Integer targetFolderId, Integer userId, String userName) {
        //在目标文件夹下创建新文件
        File pastedFile=new File();
        pastedFile.setFilename(copiedFile.getFilename());
        pastedFile.setReference(copiedFile.getReference());
        pastedFile.setUserId(userId);
        pastedFile.setUserName(userName);
        pastedFile.setIsEncrypted(copiedFile.getIsEncrypted());
        pastedFile.setFolderId(targetFolderId);
        //如果是同一用户，则设置相关属性，否则不暴露其他属性
        pastedFile.setFilepath("No authority");
        pastedFile.setBatchId("No authority");
        if(Objects.equals(userId,copiedFile.getUserId())){
            pastedFile.setFilepath(copiedFile.getFilepath());
            pastedFile.setBatchId(copiedFile.getBatchId());
        }
        boolean save = this.save(pastedFile);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库插入错误");
        }
    }


}




