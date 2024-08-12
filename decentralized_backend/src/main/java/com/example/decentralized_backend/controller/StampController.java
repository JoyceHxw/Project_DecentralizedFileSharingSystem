package com.example.decentralized_backend.controller;

import com.example.decentralized_backend.domain.request.BuyStampRequest;
import com.example.decentralized_backend.domain.request.DiluteRequest;
import com.example.decentralized_backend.domain.request.TopUpRequest;
import com.example.decentralized_backend.exception.BusinessException;
import com.example.decentralized_backend.service.StampService;
import com.example.decentralized_backend.utils.Result;
import com.example.decentralized_backend.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
@RestController
@RequestMapping("stamp")
public class StampController {
    @Autowired
    private StampService stampService;

    /**
     * 查询账户下的邮票（查询swarm再更新数据库）
     * @param userId 用户id
     * @param accountId 账户id
     * @param request 请求参数
     * @return result
     */
    @GetMapping("find")
    public Result find(@RequestParam Integer userId, @RequestParam Integer accountId, HttpServletRequest request){
        return stampService.find(userId, accountId, request);
    }

    /**
     * 购买邮票
     * @param buyStampRequest 邮票相关参数
     * @param request 请求参数
     * @return result
     */
    @PostMapping("buy")
    public Result buy(@RequestBody BuyStampRequest buyStampRequest, HttpServletRequest request){
        if(buyStampRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return stampService.buy(buyStampRequest,request);
    }

    /**
     * 延长邮票有效时间
     * @param topUpRequest 邮票信息
     * @param request 请求参数
     * @return result
     */
    @PostMapping("topUp")
    public Result topUp(@RequestBody TopUpRequest topUpRequest, HttpServletRequest request){
        if(topUpRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return stampService.topUp(topUpRequest,request);
    }

    /**
     * 增加邮票容量
     * @param diluteRequest 邮票信息
     * @param request 请求参数
     * @return result
     */
    @PostMapping("dilute")
    public Result dilute(@RequestBody DiluteRequest diluteRequest, HttpServletRequest request){
        if(diluteRequest==null){
            throw new BusinessException(ResultCodeEnum.IS_NULL,"参数为空");
        }
        return stampService.dilute(diluteRequest,request);
    }
}
