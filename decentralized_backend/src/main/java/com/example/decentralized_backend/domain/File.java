package com.example.decentralized_backend.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件表
 * @TableName file
 */
@TableName(value ="file")
@Data
public class File implements Serializable {
    /**
     * 文件id
     */
    @TableId(type = IdType.AUTO)
    private Integer fileId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户名称，昵称或用户名
     */
    private String userName;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件路径
     */
    private String filepath;

    /**
     * 文件在swarm中的编号
     */
    private String reference;

    /**
     * 邮票的batch_id
     */
    private String batchId;

    /**
     * 是否加密
     */
    private Integer isEncrypted;

    /**
     * 是否过期（所附着的邮票过期，则文件会被清理）：0：有效，1：过期
     */
    private Integer isExpired;

    /**
     * 所属文件夹id
     */
    private Integer folderId;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}