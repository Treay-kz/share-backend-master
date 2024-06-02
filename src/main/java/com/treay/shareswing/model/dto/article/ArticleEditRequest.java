package com.treay.shareswing.model.dto.article;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑文章请求
 *
 * treay
 * 
 */
@Data
public class ArticleEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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