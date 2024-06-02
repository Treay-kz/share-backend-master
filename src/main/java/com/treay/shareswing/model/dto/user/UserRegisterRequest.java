package com.treay.shareswing.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建用户请求
 *
 * treay
 * 
 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 用户邮件
     */
    private String userEmail;
    /**
     * 用户验证码
     */
    private String code;
    /**
     * 用户账号
     */
    private String codingId;
    /**
     * 用户校验密码
     */
    private String checkPassword;

    private static final long serialVersionUID = 1L;
}