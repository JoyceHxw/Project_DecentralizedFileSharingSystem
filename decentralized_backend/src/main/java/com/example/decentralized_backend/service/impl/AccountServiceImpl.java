package com.example.decentralized_backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.decentralized_backend.domain.Account;
import com.example.decentralized_backend.domain.request.StakeRequest;
import com.example.decentralized_backend.domain.request.AddAccountRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.service.AccountService;
import com.example.decentralized_backend.mapper.AccountMapper;
import com.example.decentralized_backend.service.UserService;
import com.example.decentralized_backend.utils.HttpUtil;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author 81086
* @description 针对表【account(账户表)】的数据库操作Service实现
* @createDate 2024-03-15 22:31:32
*/
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
    implements AccountService{

    @Autowired
    private UserService userService;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Result wallet(Integer userId, Integer accountId, HttpServletRequest request) {
        Account account = verifyAccount(userId, accountId, request);
        //发送请求
        String urlPath="http://"+account.getHostname()+":1635/wallet";
        try{
            URIBuilder uriBuilder = new URIBuilder(urlPath);
            String dataStr = HttpUtil.httpGet(uriBuilder, null);
            JSONObject jsonObject = JSON.parseObject(dataStr);
            String balance = jsonObject.getString("bzzBalance");
            //更新账户
            account.setBalance(balance);
            int i=accountMapper.updateById(account);
            if(i==0){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库更新失败，请尝试重试");
            }
            return Result.ok(balance);
        } catch(Exception e){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
        }
    }

    /**
     * 充值和购买邮票时使用，查询余额
     * @param hostname 主机号
     * @return
     */
    public String getWallet(String hostname) {
        //发送请求
        String urlPath="http://"+hostname+":1635/wallet";
        try{
            URIBuilder uriBuilder = new URIBuilder(urlPath);
            String dataStr = HttpUtil.httpGet(uriBuilder, null);
            JSONObject jsonObject = JSON.parseObject(dataStr);
            return jsonObject.getString("bzzBalance");
        } catch(Exception e){
            return null;
        }
    }

    @Override
    public Result stake(StakeRequest stakeRequest, HttpServletRequest request) {
        Integer userId = stakeRequest.getUserId();
        Integer accountId = stakeRequest.getAccountId();
        Long amount = stakeRequest.getAmount();
        Account account = verifyAccount(userId, accountId, request);
        //发送请求
        //等待响应时间较长，设定两分钟时限
        String urlPath="http://"+ account.getHostname()+":1635/stake/"+amount;
        String dataStr = HttpUtil.httpPost(urlPath, null, null);
        //更新账户余额
        String balance = getWallet(account.getHostname());
        account.setBalance(balance);
        accountMapper.updateById(account);
        return Result.ok(dataStr);
    }

    @Override
    public Result add(AddAccountRequest addAccountRequest, HttpServletRequest request) {
        String address = addAccountRequest.getAccountAddress();
        String hostname = addAccountRequest.getHostname();
        Integer userId = addAccountRequest.getUserId();
        //验证是否为空
        if(address==null || userId==null || hostname==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"存在参数为空");
        }
        //验证是否登录
        userService.verifyAuth(userId,request);
        //验证账户是否存在
        LambdaQueryWrapper<Account> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getAccountAddress,address);
        Long count = accountMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"账户已存在");
        }
        //todo:检验地址是否有效
        //插入数据库
        Account account=new Account();
        account.setAccountAddress(address);
        account.setHostname(hostname);
        account.setUserId(userId);
        //保存
        boolean save = this.save(account);
        if(!save){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据插入失败，添加账户失败");
        }
        return Result.ok(account);
    }

    @Override
    public Result find(Integer userId, HttpServletRequest request) {
        //验证是否登录
        userService.verifyAuth(userId,request);
        //查询数据库
        LambdaQueryWrapper<Account> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId,userId);
        List<Account> accountList = accountMapper.selectList(queryWrapper);
        //更新一遍余额，但是太慢了，不好
//        for(Account account: accountList){
//            String balance = getWallet(account.getHostname());
//            if(balance==null){
//                continue;
//            }
//            account.setBalance(balance);
//        }
        return Result.ok(accountList);
    }

    @Override
    public Result update(Account account, HttpServletRequest request) {
        //验证是否登录
        userService.verifyAuth(account.getUserId(),request);
        //更新数据库
        // 获取当前时间的毫秒数
        long currentTimeMillis = System.currentTimeMillis();
        // 创建Date对象表示当前时间
        Date currentDate = new Date(currentTimeMillis);
        account.setUpdateTime(currentDate);
        int i = accountMapper.updateById(account);
        if(i==0){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据库更新失败");
        }
        return Result.ok("","更新成功");
    }

    @Override
    public Result delete(Integer userId, Integer accountId, HttpServletRequest request) {
        verifyAccount(userId, accountId, request);
        //从数据库中删除
        boolean result = this.removeById(accountId);
        if(!result){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"删除失败");
        }
        return Result.ok(null,"删除成功");
    }

    @Override
    public Account verifyAccount(Integer userId, Integer accountId, HttpServletRequest request){
        //验证是否登录
        userService.verifyAuth(userId, request);
        //检验账户是否属于该用户
        Account account = accountMapper.selectById(accountId);
        if(account==null){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"该账户不存在");
        }
        if(!Objects.equals(account.getUserId(), userId)){
            throw new BusinessException(ResultCodeEnum.NO_AUTH,"没有该账户的权限");
        }
        return account;
    }
}




