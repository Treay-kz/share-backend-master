package com.treay.shareswing.model.dto.article;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @auther Treay_kz
 * @Date 2024/6/8 9:12
 */
@Data
public class ArticlePublishRequest implements Serializable {
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
     * 标签列表（json 数组）
     */
    private String tags;

    /**
     * 文章状态: 0-待审核 1-审核未通过 2-已发布,0
     */
    private Integer articleStatus;

    /**
     * 创建用户 id
     */
    private Long userId;

;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
