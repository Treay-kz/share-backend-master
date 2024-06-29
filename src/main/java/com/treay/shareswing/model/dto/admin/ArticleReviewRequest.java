package com.treay.shareswing.model.dto.admin;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @auther Treay_kz
 * @Date 2024/6/21 13:42
 */
@Data
public class ArticleReviewRequest implements Serializable {

    /**
     *  id
     */
    private Long id;

    /**
     * 是否通过
     */
    private Boolean isPass;
    /**
     * 未通过原因
     */
    private String reviewMessage ;

    /**
     * 未通过原因描述
     */
    private String reviewDescription;



    private static final long serialVersionUID = 1L;
}
