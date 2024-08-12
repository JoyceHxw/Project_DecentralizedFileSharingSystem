package com.example.decentralized_backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.decentralized_backend.domain.Account;
import com.example.decentralized_backend.domain.Stamp;
import com.example.decentralized_backend.domain.request.BuyStampRequest;
import com.example.decentralized_backend.domain.request.DiluteRequest;
import com.example.decentralized_backend.domain.request.TopUpRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.mapper.AccountMapper;
import com.example.decentralized_backend.mapper.StampMapper;
import com.example.decentralized_backend.service.AccountService;
import com.example.decentralized_backend.service.StampService;
import com.example.decentralized_backend.service.UserService;
import com.example.decentralized_backend.utils.HttpUtil;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

/**
* @author 81086
* @description 针对表【stamp(邮票表)】的数据库操作Service实现
* @createDate 2024-03-18 21:15:57
*/
@Service
public class StampServiceImpl extends ServiceImpl<StampMapper, Stamp>
    implements StampService{

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private StampMapper stampMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) //数据库事务回滚
    public Result find(Integer userId, Integer accountId, HttpServletRequest request) {
        //STEP 1: 先向节点发送请求，查询账户下的stamp
        //1.1验证是否登录，1.2验证是否具有账户
        Account account = accountService.verifyAccount(userId, accountId, request);
        //1.3发送请求
        String urlPath="http://"+account.getHostname()+":1635/stamps";
        String dataStr;
        try{
            URIBuilder uriBuilder = new URIBuilder(urlPath);
            dataStr = HttpUtil.httpGet(uriBuilder, null);
        } catch(Exception e){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,e.getMessage());
        }
        //STEP 2: 再更新数据库的信息
        //2.1转换数据格式
        JSONObject jsonObject = JSON.parseObject(dataStr);
        String stampsData=jsonObject.getString("stamps");
        JSONArray stampListJSON = JSONObject.parseArray(stampsData);
        //2.2更新数据库
        ArrayList<Stamp> stampList=new ArrayList<>();
        for(Object stampObj: stampListJSON){
            JSONObject jObj = (JSONObject) stampObj;
            String batchID = jObj.getString("batchID");
            String exists = jObj.getString("exists");
            String batchTTL = jObj.getString("batchTTL");
            Long ttl = Long.parseLong(batchTTL);
            String depth = jObj.getString("depth");
            Integer depthInt=Integer.parseInt(depth);
            String amount = jObj.getString("amount");
            Long amountLong = Long.parseLong(amount);

            LambdaQueryWrapper<Stamp> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Stamp::getBatchId,batchID);
            Stamp stamp = stampMapper.selectOne(queryWrapper);
            //如果是空，则加入数据库中
            if(stamp==null){
                Stamp stampNew=new Stamp();
                stampNew.setBatchId(batchID);
                stampNew.setAccountId(accountId);
                stampNew.setAmount(amountLong);
                stampNew.setDepth(depthInt);

                String immutableFlag = jObj.getString("immutableFlag");
                if(immutableFlag.equals("true")){
                    stampNew.setType(0);
                }
                else{
                    stampNew.setType(1);
                }

                if(exists.equals("true")){
                    stampNew.setIsExpired(0);
                }
                else{
                    stampNew.setIsExpired(1);
                }

                stampNew.setTtl(ttl);

                //存入数据库
                boolean save = this.save(stampNew);
                if(!save){
                    throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据插入失败");
                }
                //加入结果列表
                stampList.add(stampNew);
            }
            //否则更新是否有效和ttl信息
            else{
                if(exists.equals("true")){
                    stamp.setIsExpired(0);
                }
                else{
                    stamp.setIsExpired(1);
                }

                stamp.setAmount(amountLong);
                stamp.setDepth(depthInt);
                stamp.setTtl(ttl);

                stampList.add(stamp);
            }
        }
        //已经过期的邮票需要在数据库中标注，请求不会返回已过期的邮票
        LambdaQueryWrapper<Stamp> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Stamp::getAccountId,account.getAccountId());
        for (Stamp stampSQL : stampMapper.selectList(queryWrapper)) {
            boolean isFlag=false;
            for(Stamp stamp: stampList){
                if(Objects.equals(stampSQL.getBatchId(),stamp.getBatchId())){
                    isFlag=true;
                    break;
                }
            }
            //数据库中的邮票没有出现在请求返回的结果中，需要删除
            if(!isFlag){
                this.removeById(stampSQL.getStampId());
            }
        }
        return Result.ok(stampList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) //数据库事务回滚
    public Result buy(BuyStampRequest buyStampRequest, HttpServletRequest request) {
        Integer userId = buyStampRequest.getUserId();
        Integer accountId = buyStampRequest.getAccountId();
        Integer depth = buyStampRequest.getDepth();
        Long amount = buyStampRequest.getAmount();
        //STEP1：发送请求
        //1.1验证是否登录，1.2验证是否具有账户
        Account account = accountService.verifyAccount(userId, accountId, request);
        //1.3发送请求
        String urlPath="http://"+ account.getHostname()+":1635/stamps/"+amount+"/"+depth;
        String dataStr = HttpUtil.httpPost(urlPath, null, null);
        //STEP2：如果购买成功，保存到数据库，如果失败，抛出错误
        //2.1转换数据格式
        JSONObject jsonObject = JSON.parseObject(dataStr);
        String batchID=jsonObject.getString("batchID");
        if(batchID==null){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"购买失败");
        }
        else{
            Stamp stamp=new Stamp();
            stamp.setBatchId(batchID);
            stamp.setAmount(amount);
            stamp.setDepth(depth);
            stamp.setAccountId(accountId);
            //不可变的
            stamp.setType(0);
            stamp.setIsExpired(0);
            boolean save = this.save(stamp);
            if(!save){
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"数据插入失败，请直接查询邮票");
            }
            //更新账户余额
            String balance = accountService.getWallet(account.getHostname());
            account.setBalance(balance);
            accountMapper.updateById(account);
            return Result.ok(stamp);
        }
    }

    @Override
    public Result topUp(TopUpRequest topUpRequest, HttpServletRequest request) {
        Integer userId = topUpRequest.getUserId();
        Integer accountId = topUpRequest.getAccountId();
        String batchId = topUpRequest.getBatchId();
        Long amount = topUpRequest.getAmount();
        //检验
        Account account = accountService.verifyAccount(userId, accountId, request);
        //发送请求
        String urlPath="http://"+ account.getHostname()+":1635/stamps/topup/"+batchId+"/"+amount;
        String dataStr = HttpUtil.httpPatch(urlPath, null, null);
        JSONObject jsonObject = JSON.parseObject(dataStr);
        String txHash=jsonObject.getString("txHash");
        if(txHash==null){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"操作失败");
        }
        return Result.ok(txHash);
    }

    @Override
    public Result dilute(DiluteRequest diluteRequest, HttpServletRequest request) {
        Integer userId = diluteRequest.getUserId();
        Integer accountId = diluteRequest.getAccountId();
        String batchId = diluteRequest.getBatchId();
        Long depth = diluteRequest.getDepth();
        //检验权限
        Account account = accountService.verifyAccount(userId, accountId, request);
        //检验depth是不是大于原来的depth
        LambdaQueryWrapper<Stamp> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Stamp::getBatchId,batchId);
        Stamp stamp = stampMapper.selectOne(queryWrapper);
        if(stamp.getDepth()>=depth){
            throw new BusinessException(ResultCodeEnum.PARAMS_ERROR,"深度应大于当前邮票深度");
        }
        //发送请求
        String urlPath="http://"+ account.getHostname()+":1635/stamps/dilute/"+batchId+"/"+depth;
        String dataStr = HttpUtil.httpPatch(urlPath, null, null);
        JSONObject jsonObject = JSON.parseObject(dataStr);
        String txHash=jsonObject.getString("txHash");
        if(txHash==null){
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR,"操作失败");
        }
        return Result.ok(txHash);
    }

}




