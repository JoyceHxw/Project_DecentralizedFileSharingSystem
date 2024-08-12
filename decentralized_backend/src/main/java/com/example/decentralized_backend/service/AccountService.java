package com.example.decentralized_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.decentralized_backend.domain.Account;
import com.example.decentralized_backend.domain.request.StakeRequest;
import com.example.decentralized_backend.domain.request.AddAccountRequest;
import com.example.decentralized_backend.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigInteger;

/**
* @author 81086
* @description 针对表【account(账户表)】的数据库操作Service
* @createDate 2024-03-15 22:31:32
*/
public interface AccountService extends IService<Account> {

    Result wallet(Integer userId, Integer accountId, HttpServletRequest request);

    String getWallet(String hostname);

    Result stake(StakeRequest stakeRequest, HttpServletRequest request);

    Result add(AddAccountRequest addAccountRequest, HttpServletRequest request);

    Result find(Integer userId, HttpServletRequest request);

    Result update(Account account, HttpServletRequest request);

    Result delete(Integer userId, Integer accountId, HttpServletRequest request);

    Account verifyAccount(Integer userId, Integer accountId, HttpServletRequest request);
}
