package com.treay.shareswing.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新用户请求
 *
 * treay
 * 
 */
@Data
public class UserUpdateRequest implements Serializable {

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