package com.example.decentralized_backend.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账户表
 * @TableName account
 */
@TableName(value ="account")
@Data
public class Account implements Serializable {
    /**
     * 账户id
     */
    @TableId(type = IdType.AUTO)
    private Integer accountId;

    /**
     * 账户在区块链上的地址
     */
    private String accountAddress;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * swarm运行的主机号
     */
    private String hostname;

    /**
     * 钱包余额
     */
    private String balance;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}