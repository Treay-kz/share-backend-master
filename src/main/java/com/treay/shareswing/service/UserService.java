package com.treay.shareswing.service;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.common.BaseResponse;
import com.treay.shareswing.model.dto.user.UserSendEmail;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;


/**
* @author 16799
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-06-01 11:50:40
*/
public interface UserService extends IService<User> {

    /**
     * 发送邮件
     * @param userSendEmail
     * @return
     */
    BaseResponse<Boolean> sendEmail(UserSendEmail userSendEmail);

    /**
     * 用户注册
     * @param userAccount
     * @param userEmail
     * @param code
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userEmail, String code, String userPassword, String checkPassword,String codingId);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);



    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);
}
