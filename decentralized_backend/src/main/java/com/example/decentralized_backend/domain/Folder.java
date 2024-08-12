package com.example.decentralized_backend.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文件夹
 * @TableName folder
 */
@TableName(value ="folder")
@Data
public class Folder implements Serializable {
    /**
     * 文件夹id
     */
    @TableId(type = IdType.AUTO)
    private Integer folderId;

    /**
     * 文件夹名称
     */
    private String folderName;

    /**
     * 父文件夹id
     */
    private Integer parentFolderId;

    /**
     * 创建者
     */
    private Integer userId;

    /**
     * 创建者名称
     */
    private String userName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}