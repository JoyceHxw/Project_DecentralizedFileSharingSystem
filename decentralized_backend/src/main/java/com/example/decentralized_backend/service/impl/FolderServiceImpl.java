package com.example.decentralized_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.decentralized_backend.domain.File;
import com.example.decentralized_backend.domain.Folder;
import com.example.decentralized_backend.domain.Team;
import com.example.decentralized_backend.domain.User;
import com.example.decentralized_backend.domain.request.CopyFolderRequest;
import com.example.decentralized_backend.domain.request.FolderCreateRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.mapper.FileMapper;
import com.example.decentralized_backend.mapper.TeamMapper;
import com.example.decentralized_backend.service.FileService;
import com.example.decentralized_backend.service.FolderService;
import com.example.decentralized_backend.mapper.FolderMapper;
import com.example.decentralized_backend.service.TeamService;
import com.example.decentralized_backend.service.UserService;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
* @author 81086
* @description 针对表【folder(文件夹)】的数据库操作Service实现
* @createDate 2024-04-15 15:21:20
*/
@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder>
    implements FolderService{

    @Autowired
    private UserService userService;

    @Autowired
    private FolderMapper folderMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileService fileService;

    @Override
    public Integer createFolder(FolderCreateRequest folderRequest, HttpServletRequest request) {
        Integer userId = folderRequest.getUserId();
        String folderName = folderRequest.getFolderName();
        Integer parentFolderId = folderRequest.getParentFolderId();
        //检查权限
        userService.verifyAuth(userId,request);
        User currentUser = userService.getCurrentUser(request);
        //检查参数
        //文件名不能为空
        if(StringUtils.isBlank(folderName)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"文件名不能为空");
        }
        if(parentFolderId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"父文件夹参数为空");
        }
        //检查父目录是否存在，排除根目录
        if(!Objects.equals(parentFolderId,0)){
            LambdaQueryWrapper<Folder> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Folder::getFolderId,parentFolderId);
            List<Folder> folderList = folderMapper.selectList(queryWrapper);
            if(folderList.isEmpty()){
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"父文件夹不存在");
            }
            //检查当前群组的当前目录下文件名是否重读
            //注意排除父目录为0的根目录
            for(Folder folderExist: folderList){
                if(Objects.equals(folderExist.getFolderName(),folderName)){
                    throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"文件夹名字重复");
                }
            }
        }
        Folder folder=new Folder();
        folder.setFolderName(folderName);
        folder.setParentFolderId(parentFolderId);
        folder.setUserId(userId);
        folder.setUserName(currentUser.getNickname()==null? currentUser.getUsername():currentUser.getNickname());
        boolean save = this.save(folder);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据插入失败，请重新创建");
        }
        return folder.getFolderId();
    }

    @Override
    public Result deleteFolder(Integer userId, Integer folderId, HttpServletRequest request) {
        if(userId==null || folderId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        //检查权限
        userService.verifyAuth(userId,request);
        //检查是否是文件夹的创建者
        LambdaQueryWrapper<Folder> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Folder::getFolderId,folderId);
        Folder folder = folderMapper.selectOne(queryWrapper);
        if(folder==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"文件夹不存在");
        }
        if(!Objects.equals(userId,folder.getUserId())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有该文件夹的权限");
        }
        //删除文件夹
        int i = folderMapper.deleteById(folder);
        if(i==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库删除错误，请重新删除");
        }
        return Result.ok(null,"文件夹删除成功");
    }

    @Override
    public Result findFolder(Integer userId, Integer parentFolderId, HttpServletRequest request) {
        userService.verifyAuth(userId,request);
        if(parentFolderId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        //获取列表
        LambdaQueryWrapper<Folder> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Folder::getParentFolderId,parentFolderId);
        queryWrapper.orderByAsc(Folder::getFolderName);
        List<Folder> folderList = folderMapper.selectList(queryWrapper);
        return Result.ok(folderList);
    }

    @Override
    public Result copy(CopyFolderRequest copyFolderRequest, HttpServletRequest request) {
        Integer userId = copyFolderRequest.getUserId();
        Integer copiedFolderId = copyFolderRequest.getCopiedFolderId();
        Integer targetFolderId = copyFolderRequest.getTargetFolderId();
        //验证登录
        userService.verifyAuth(userId, request);
        User currentUser = userService.getCurrentUser(request);
        String currentUserName=currentUser.getNickname()==null? currentUser.getUsername(): currentUser.getNickname();
        //检查被拷贝的文件夹是否存在
        if(copiedFolderId==null || targetFolderId==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"拷贝文件夹参数为空");
        }
        LambdaQueryWrapper<Folder> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Folder::getFolderId,copiedFolderId);
        Folder copiedFolder = folderMapper.selectOne(queryWrapper);
        if(copiedFolder==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"拷贝文件夹不存在");
        }
        //检查目标文件夹是否存在
        LambdaQueryWrapper<Folder> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(Folder::getFolderId,targetFolderId);
        Folder targetFolder = folderMapper.selectOne(queryWrapper1);
        if(targetFolder==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"目标文件夹不存在");
        }
        //检查目标文件夹下有没有同名的文件夹
        LambdaQueryWrapper<Folder> queryWrapper2=new LambdaQueryWrapper<>();
        queryWrapper2.eq(Folder::getParentFolderId,targetFolderId);
        for (Folder folder : folderMapper.selectList(queryWrapper2)) {
            if(Objects.equals(folder.getFolderName(),copiedFolder.getFolderName())){
                throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"拷贝失败，存在同名文件夹");
            }
        }
        //加分布式锁，防止并发问题
        //需要加锁，防止多个线程不安全，采用悲观锁
        //针对同一用户加synchronized，锁到事务提交
        //toString确保在多线程环境下，不同的 userId 对象产生的字符串在比较时是相等的。
        //intern将指定的字符串对象的引用保存在字符串常量池中，确保只有一个相同内容的字符串对象存在于常量池中
        //todo:改成分布式锁
//        RLock lock=redissonClient.getLock(FILE_FOLDER_LOCK_KEY);
//        try {
//            //获取锁
//            boolean isLock = lock.tryLock(0, -1, TimeUnit.MILLISECONDS);
//            if(!isLock){
//                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"有人正在操作，请稍后尝试");
//            }
//            //执行业务
//            FolderService proxy =(FolderService) AopContext.currentProxy();
//            proxy.copyFolderRecursion(copiedFolder,targetFolderId,userId,currentUserName);
//            return Result.ok(null,"拷贝文件夹成功");
//        }catch (InterruptedException e) {
//            log.error("doCacheRecommendUser error",e);
//            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"分布式锁获取错误");
//        } finally {
//            //释放锁
//            lock.unlock();
//        }
        synchronized ((userId.toString()).intern()){
            //直接调用而不是通过代理调用会使事务失效
            FolderService proxy =(FolderService) AopContext.currentProxy();
            proxy.copyFolderRecursion(copiedFolder,targetFolderId,userId,currentUserName);
        }
        return Result.ok(null,"拷贝文件夹成功");
    }

    /**
     * 递归拷贝文件夹
     * @param copiedFolder 原文件夹
     * @param targetFolderId 目的文件夹id
     * @param userId 用户id
     * @param userName 用户名称
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyFolderRecursion(Folder copiedFolder, Integer targetFolderId, Integer userId, String userName){
        //先创建此文件夹
        Folder pastedFolder=new Folder();
        pastedFolder.setFolderName(copiedFolder.getFolderName());
        pastedFolder.setParentFolderId(targetFolderId);
        pastedFolder.setUserId(userId);
        pastedFolder.setUserName(userName);
        boolean save = this.save(pastedFolder);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库保存错误");
        }
        //拷贝子文件夹
        LambdaQueryWrapper<Folder> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Folder::getParentFolderId,pastedFolder.getFolderId());
        for (Folder folder : folderMapper.selectList(queryWrapper)) {
            //进入该文件夹继续拷贝
            copyFolderRecursion(folder,pastedFolder.getFolderId(),userId,userName);
        }
        //拷贝子文件
        LambdaQueryWrapper<File> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(File::getFolderId,copiedFolder.getFolderId());
        for (File file : fileMapper.selectList(queryWrapper1)) {
            fileService.copyFile(file,pastedFolder.getFolderId(),userId,userName);
        }
    }
}




