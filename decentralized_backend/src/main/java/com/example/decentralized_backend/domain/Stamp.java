package com.example.decentralized_backend.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

import java.io.Serializable;

/**
 * 邮票表
 * @TableName stamp
 */
@TableName(value ="stamp")
@Data
public class Stamp implements Serializable {
    /**
     * 邮票id
     */
    @TableId(type = IdType.AUTO)
    private Integer stampId;

    /**
     * 邮票在区块链上的id
     */
    private String batchId;

    /**
     * 一个块中包含的PLUR个数
     */
    private Long amount;

    /**
     * 一个批次可以存储多少数据（块）
     */
    private Integer depth;

    /**
     * 邮票类型：0：不可变的；1：可变的
     */
    private Integer type;

    /**
     * 账户id
     */
    private Integer accountId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 是否过期：0：有效；1：过期
     */
    private Integer isExpired;

    /**
     * 邮票的有效时间
     */
    private Long ttl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}