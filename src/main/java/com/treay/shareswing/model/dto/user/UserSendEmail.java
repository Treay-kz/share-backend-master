package com.treay.shareswing.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:Treay
 *
 */
@Data
public class UserSendEmail implements Serializable {

    private static final long serialVersionUID = 46412442243484364L;

    private String userEmail;
    private String code;

}