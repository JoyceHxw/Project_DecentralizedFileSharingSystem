package com.example.decentralized_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.decentralized_backend.domain.Stamp;
import com.example.decentralized_backend.domain.request.BuyStampRequest;
import com.example.decentralized_backend.domain.request.DiluteRequest;
import com.example.decentralized_backend.domain.request.TopUpRequest;
import com.example.decentralized_backend.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 81086
* @description 针对表【stamp(邮票表)】的数据库操作Service
* @createDate 2024-03-18 21:15:57
*/
public interface StampService extends IService<Stamp> {

    Result find(Integer userId, Integer accountId, HttpServletRequest request);

    Result buy(BuyStampRequest buyStampRequest, HttpServletRequest request);

    Result topUp(TopUpRequest topUpRequest, HttpServletRequest request);

    Result dilute(DiluteRequest diluteRequest, HttpServletRequest request);
}
