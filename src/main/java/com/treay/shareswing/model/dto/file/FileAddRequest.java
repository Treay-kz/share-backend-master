package com.treay.shareswing.model.dto.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建文件请求
 *
 * treay
 *
 */
@Data
public class FileAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}