package com.treay.shareswing.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户请求
 *
 * treay
 * 
 */
@Data
public class UserUpdatePasswordRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户邮箱
     */
    private String code;


    private static final long serialVersionUID = 1L;
}