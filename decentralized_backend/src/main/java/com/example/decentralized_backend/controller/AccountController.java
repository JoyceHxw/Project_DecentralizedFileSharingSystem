package com.example.decentralized_backend.controller;

import com.example.decentralized_backend.domain.Account;
import com.example.decentralized_backend.domain.request.StakeRequest;
import com.example.decentralized_backend.domain.request.AddAccountRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.service.AccountService;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@RestController
@RequestMapping("account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    /**
     * 查询区块链账户钱包余额
     * @param userId 用户id
     * @param accountId 账户id
     * @param request 请求参数
     * @return result
     */
    @GetMapping("wallet")
    public Result wallet(@RequestParam Integer userId, @RequestParam Integer accountId, HttpServletRequest request) {
        return accountService.wallet(userId,accountId,request);
    }

    /**
     * 给节点充值BZZ
     * @param stakeRequest 账户信息参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("stake")
    public Result stake(@RequestBody StakeRequest stakeRequest, HttpServletRequest request){
        if(stakeRequest ==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return accountService.stake(stakeRequest,request);
    }

    /**
     * 添加swarm节点账户
     * @param addAccountRequest 账户信息参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("add")
    public Result add(@RequestBody AddAccountRequest addAccountRequest, HttpServletRequest request){
        if(addAccountRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return accountService.add(addAccountRequest, request);
    }

    /**
     * 查询用户名下的所有账户
     * @param userId 用户id
     * @param request 请求参数
     * @return result
     */
    @GetMapping("find")
    public Result find(@RequestParam Integer userId, HttpServletRequest request){
        return accountService.find(userId, request);
    }
    //todo:修改账户，删除账户

    /**
     * 更新账户信息
     * @param account 账户信息参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("update")
    public Result update(@RequestBody Account account, HttpServletRequest request){
        if(account==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return accountService.update(account,request);
    }

    /**
     * 删除账户
     * @param accountId 账户Id
     * @param request 请求参数
     * @return result
     */
    @PostMapping("delete")
    public Result delete(@RequestParam Integer userId, @RequestParam Integer accountId, HttpServletRequest request){
        return accountService.delete(userId,accountId,request);
    }
}
