package com.example.decentralized_backend.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.decentralized_backend.domain.User;
import com.example.decentralized_backend.domain.request.LoginRequest;
import com.example.decentralized_backend.domain.request.RegisterRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.service.UserService;
import com.example.decentralized_backend.mapper.UserMapper;
import com.example.decentralized_backend.utils.MD5Util;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author 81086
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-03-14 21:50:59
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Autowired
    private UserMapper userMapper;

    //密码加密加盐
    private static final String SALT="swarm";

    //用户登录态
    private static final String USER_LOGIN_STATE="userLoginState";

    @Override
    public Result register(RegisterRequest registerRequest) {
        String username=registerRequest.getUsername();
        String firstPassword=registerRequest.getFirstPassword();
        String secondPassword=registerRequest.getSecondPassword();
        //检验非空
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(firstPassword) || StringUtils.isEmpty(secondPassword)){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"账号或密码为空");
        }
        //检验账户长度>=6
        if(username.length()<6){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号长度小于6");
        }
        //检验密码长度>=6
        if(firstPassword.length()<6 || secondPassword.length()<6){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码长度小于6");
        }
        //账户不能包含特殊字符
        String regex = "^[a-zA-Z0-9_.@]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        if(!matcher.matches()) {
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号包含特殊字符");
        }
        //检验两次输入的密码相同
        if(!firstPassword.equals(secondPassword)){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"两次密码不一致");
        }
        //检验账户名不能重复
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        Long count = userMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账号已存在");
        }
        User user=new User();
        user.setUsername(username);
        //密码加密
        user.setPassword(MD5Util.encrypt(SALT+firstPassword));
        //保存
        boolean save = this.save(user);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据插入失败，注册失败");
        }
        return Result.ok(null,"注册成功");
    }

    @Override
    public Result login(LoginRequest loginRequest, HttpServletRequest request) {
        String username=loginRequest.getUsername();
        String password=loginRequest.getPassword();
        //检验非空
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"用户名或密码为空");
        }
        //检验用户名存在
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"用户名不存在");
        }
        //检验密码正确
        String encryptedPassword = MD5Util.encrypt(SALT + password);
        if(!encryptedPassword.equals(user.getPassword())){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"密码错误");
        }
        user.setPassword("");
        //在session中记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        return Result.ok(user,"登录成功");
    }

    @Override
    public Result logout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return Result.ok(null,"退出成功");
    }

    @Override
    public Result updateUser(User user, HttpServletRequest request) {
        verifyAuth(user.getUserId(), request);
        //更新数据库
        // 获取当前时间的毫秒数
        long currentTimeMillis = System.currentTimeMillis();
        // 创建Date对象表示当前时间
        Date currentDate = new Date(currentTimeMillis);
        user.setUpdateTime(currentDate);
        int i = userMapper.updateById(user);
        if(i==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库更新失败");
        }
        return Result.ok("","更新成功");
    }

    /**
     * 检查权限，是否登录和是否一致
     * @param userId 提供的用户id
     * @param request 请求参数获取的当前用户
     */
    @Override
    public void verifyAuth(Integer userId, HttpServletRequest request) {
        //获取当前用户信息，比较是否一致
        User currentUser = getCurrentUser(request);
        if(currentUser==null){
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN,"未登录");
        }
        if(!Objects.equals(userId,currentUser.getUserId())){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"无权限");
        }
    }

    /**
     * 获取当前用户
     * @param request 请求参数
     * @return User
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        //从session中获取信息
        if(request==null){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR);
        }
        Object object = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user=(User) object;
        if(user==null){
            throw new BusinessException(ResultCodeEnum.NOT_LOGIN,"未登录");
        }
        //更新用户信息
        Integer userId = user.getUserId();
        User userUpdated = userMapper.selectById(userId);
        userUpdated.setPassword("");
        return userUpdated;
    }
}




