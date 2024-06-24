package com.treay.shareswing.model.dto.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * @auther Treay_kz
 * @Date 2024/6/22 11:32
 */
@Data
public class UserBanRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 启用/禁用
     */
    private Boolean banStatus;


    private static final long serialVersionUID = 1L;

}
