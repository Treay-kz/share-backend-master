package com.treay.shareswing.controller;

import com.treay.shareswing.common.BaseResponse;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.common.ResultUtils;

import com.treay.shareswing.exception.BusinessException;

import com.treay.shareswing.model.dto.user.UserLoginRequest;
import com.treay.shareswing.model.dto.user.UserRegisterRequest;
import com.treay.shareswing.model.dto.user.UserSendEmail;
import com.treay.shareswing.model.vo.LoginUserVO;
import com.treay.shareswing.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 用户接口
 *
 * treay
 * 
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String userEmail = userRegisterRequest.getUserEmail();
        String code = userRegisterRequest.getCode();
        String codingId = userRegisterRequest.getCodingId();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,userEmail,code)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userEmail,code,userPassword, checkPassword,codingId);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword,request);
        return ResultUtils.success(loginUserVO);
    }


    /**
     * 发送邮箱验证码
     * @param userSendEmail
     * @return
     */
    @PostMapping("/sendEmail")
    public BaseResponse<Boolean> sendEmail(@RequestBody UserSendEmail userSendEmail) {
        return userService.sendEmail(userSendEmail);
    }

}
