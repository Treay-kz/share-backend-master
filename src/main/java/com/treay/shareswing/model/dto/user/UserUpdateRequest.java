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
     * 用户昵称
     */
    private String userName;
    /**
     * 昵称
     */
    private String userProfile;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    /**
     * 手机号码
     */
    private String phone;

    private static final long serialVersionUID = 1L;
}