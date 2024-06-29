package com.treay.shareswing.service;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.treay.shareswing.common.BaseResponse;
import com.treay.shareswing.model.dto.user.UserQueryRequest;
import com.treay.shareswing.model.dto.user.UserSendEmail;
import com.treay.shareswing.model.dto.user.UserUpdatePasswordRequest;
import com.treay.shareswing.model.entity.Article;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.model.vo.ArticleVO;
import com.treay.shareswing.model.vo.LoginUserVO;
import com.treay.shareswing.model.vo.UserVO;

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
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);



    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);
    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 根据id查询用户 返回封装类（VO）
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 修改密码
     * @param userUpdatePasswordRequest
     * @return
     */
    boolean updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest);

    /**
     * 根据用户账号查询用户
     * @param userAccount
     * @return
     */
    User getByUserAccount(String userAccount);

    /**
     * 获取验证码
     * @param email
     * @return
     */
    String getValideCode(String email);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
