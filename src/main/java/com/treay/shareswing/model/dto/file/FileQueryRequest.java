package com.treay.shareswing.model.dto.file;

import com.treay.shareswing.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}