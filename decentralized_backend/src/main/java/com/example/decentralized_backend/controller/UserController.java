package com.example.decentralized_backend.controller;

import com.example.decentralized_backend.domain.User;
import com.example.decentralized_backend.domain.request.LoginRequest;
import com.example.decentralized_backend.domain.request.RegisterRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.service.UserService;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("register")
    public Result register(@RequestBody RegisterRequest registerRequest){
        if(registerRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"注册参数为空");
        }
        return userService.register(registerRequest);
    }

    @PostMapping("login")
    public Result login(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
        if(loginRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"登录参数为空");
        }
        return userService.login(loginRequest,request);
    }

    @GetMapping("logout")
    public Result logout(HttpServletRequest request){
        if(request==null){
            return null;
        }
        return userService.logout(request);
    }

    @PostMapping("update")
    public Result updateUser(@RequestBody User user, HttpServletRequest request){
        if(user==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return userService.updateUser(user,request);
    }

    @GetMapping("getCurrentUser")
    public Result getCurrentUser(HttpServletRequest request){
        User currentUser = userService.getCurrentUser(request);
        return Result.ok(currentUser,"获取当前用户成功");
    }
}
