package com.treay.shareswing.model.dto.user;

import com.treay.shareswing.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询用户请求
 *
 * treay
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 搜索词
     */
    private String userAccount;

    /**
     * 内容
     */
    private String userName;



    private static final long serialVersionUID = 1L;
}