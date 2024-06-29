package com.treay.shareswing.model.dto.file;

import com.treay.shareswing.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询文件请求
 *
 * treay
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 上传人id
     */
    private Long userId;

    /**
     * 文件路径
     */
    private String fileUrl;

    /**
     * 文件类型（word、ppt、pdf、txt）
     */
    private String fileType;

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


    private static final long serialVersionUID = 1L;
}