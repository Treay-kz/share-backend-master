package com.treay.shareswing.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件表
 * @TableName notice
 */
@TableName(value ="notice")
@Data
public class Notice implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上传人id
     */
    private Long userId;

    /**
     * 文件路径
     */
    private Long fileUrl;

    /**
     * 文件类型（word、ppt、pdf、txt）
     */
    private Long fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件状态:0-待审核 1-审核未通过 2-已发布
     */
    private Integer fileStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}