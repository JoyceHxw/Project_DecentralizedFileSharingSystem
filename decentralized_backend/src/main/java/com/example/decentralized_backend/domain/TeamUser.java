package com.example.decentralized_backend.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 组和用户的关系表（多对多）
 * @TableName team_user
 */
@TableName(value ="team_user")
@Data
public class TeamUser implements Serializable {
    /**
     * 记录id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 组id
     */
    private Integer teamId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 是否是群组
     */
    private Integer isCreator;

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