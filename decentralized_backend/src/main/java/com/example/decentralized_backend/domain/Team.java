package com.example.decentralized_backend.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 群组表
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * 组id
     */
    @TableId(type = IdType.AUTO)
    private Integer teamId;

    /**
     * 组名
     */
    private String teamName;

    /**
     * 创建人id
     */
    private Integer userId;

    private String userName;

    /**
     * 加入组时的密码，六位数字
     */
    private String teamPassword;

    /**
     * 群组介绍
     */
    private String description;

    /**
     * 跟目录id
     * 不需要在folder表添加teamid数据，会冗余，直接设置好根目录即可
     */
    private Integer rootFolderId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
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
