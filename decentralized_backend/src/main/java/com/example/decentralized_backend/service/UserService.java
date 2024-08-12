package com.example.decentralized_backend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.decentralized_backend.domain.User;
import com.example.decentralized_backend.domain.request.LoginRequest;
import com.example.decentralized_backend.domain.request.RegisterRequest;
import com.example.decentralized_backend.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 81086
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-03-14 21:50:59
*/
public interface UserService extends IService<User> {

    Result register(RegisterRequest registerRequest);

    Result login(LoginRequest loginRequest, HttpServletRequest request);

    Result logout(HttpServletRequest request);

    Result updateUser(User user, HttpServletRequest request);

    void verifyAuth(Integer userId, HttpServletRequest request);

    User getCurrentUser(HttpServletRequest request);
}
