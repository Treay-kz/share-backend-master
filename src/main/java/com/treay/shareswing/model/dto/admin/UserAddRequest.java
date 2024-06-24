package com.treay.shareswing.model.dto.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * @auther Treay_kz
 * @Date 2024/6/22 11:32
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 账号
     */
    private String userProfile;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    /**
     * 用户角色: user, admin
     */
    private String codingId;

    private static final long serialVersionUID = 1L;

}
