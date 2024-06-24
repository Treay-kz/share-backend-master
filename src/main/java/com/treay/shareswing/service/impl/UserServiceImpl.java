package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.treay.shareswing.common.BaseResponse;
import com.treay.shareswing.common.ErrorCode;
import com.treay.shareswing.common.ResultUtils;
import com.treay.shareswing.constant.CommonConstant;
import com.treay.shareswing.exception.BusinessException;
import com.treay.shareswing.exception.ThrowUtils;
import com.treay.shareswing.mapper.UserMapper;
import com.treay.shareswing.model.dto.user.UserQueryRequest;
import com.treay.shareswing.model.dto.user.UserSendEmail;
import com.treay.shareswing.model.dto.user.UserUpdatePasswordRequest;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.model.enums.UserRoleEnum;
import com.treay.shareswing.model.vo.LoginUserVO;
import com.treay.shareswing.model.vo.UserVO;
import com.treay.shareswing.service.UserService;

import javax.mail.*;

import com.treay.shareswing.utils.EmailUtils;
import com.treay.shareswing.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sun.javafx.font.FontResource.SALT;
import static com.treay.shareswing.constant.RedisConstant.*;
import static com.treay.shareswing.constant.UserConstant.USER_LOGIN_STATE;
import static com.treay.shareswing.utils.ValidateCodeUtils.generateValidateCode;

/**
 * @author 16799
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-06-01 11:50:40
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserMapper userMapper;

    @Override
    public BaseResponse<Boolean> sendEmail(UserSendEmail userSendEmail) {
        String userEmail = userSendEmail.getUserEmail();
        String key = SEND_EMAIL_KEY + userEmail;
        if (StringUtils.isEmpty(userEmail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "email为空");
        }
        String code = generateValidateCode(6).toString();
        RLock lock = redissonClient.getLock(EMAIL_KEY + userEmail);
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                if (redisTemplate.hasKey(key)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码已发送，请勿重新再试!");
                }
                try {
                    EmailUtils.sendEmail(userEmail, code);
                } catch (MessagingException e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮件发送失败");
                }
                userSendEmail.setCode(code);
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

                try {
                    valueOperations.set(key, userSendEmail, 300000, TimeUnit.MILLISECONDS);
                    UserSendEmail sendMessage = (UserSendEmail) valueOperations.get(key);
                    log.info(sendMessage.toString());
                    return ResultUtils.success(true);
                } catch (Exception e) {
                    log.error("redis set key error", e);
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "缓存失败!");
                }
            }
        } catch (InterruptedException e) {
            log.error("redis set key error");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
        return ResultUtils.success(false);

    }

    @Override
    public long userRegister(String userAccount, String userEmail, String code, String userPassword, String checkPassword, String codingId) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userEmail, code, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        Pattern emailValidPattern = Pattern.compile("[a-zA-Z0-9]+@[A-Za-z0-9]+\\.[a-z0-9]");
        Matcher emailMatch = emailValidPattern.matcher(userEmail);
        if (!emailMatch.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号不能包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码必须相同");
        }
        if (StringUtils.isNotBlank(codingId)) {
            if (codingId.length() != 9 || codingId.charAt(0) != 'P') {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号格式错误");
            }
        }

        // 获取缓存验证码
        String sendMessageCode = this.getValideCode(userEmail);
        log.info(sendMessageCode);
        if (!code.equals(sendMessageCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不匹配!");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setEmail(userEmail);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        boolean initResult = initUser(user);
        if (!initResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        return user.getId();
    }


    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号密码不匹配");
        }
        if (!("user".equals(user.getUserRole()))) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "非学生用户或已被禁用");
        }
        // 记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    public Boolean initUser(User user) {
        String defaultUrl = "https://img1.baidu.com/it/u=1637179393,2329776654&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=542";
        user.setUserAvatar(defaultUrl);
        user.setUserName("用户" + generateValidateCode(6).toString());
        return this.updateById(user);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取用户封装
     *
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        // 对象转封装类
        UserVO userVO = UserVO.objToVo(user);
        return userVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            return null;
        }
        return (User) request.getSession().getAttribute(USER_LOGIN_STATE);
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = this.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(user.getUserRole());
        return mustRoleEnum.equals(UserRoleEnum.ADMIN);
    }

    /**
     * 更新用户密码
     *
     * @param userUpdatePasswordRequest
     * @return
     */
    @Override
    public boolean updatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest) {

        // 获取用户
        User user = this.getByUserAccount(userUpdatePasswordRequest.getUserAccount());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断输入的邮箱是否与修改用户的绑定邮箱相等
        if (!user.getEmail().equals(userUpdatePasswordRequest.getEmail())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断验证码是否相等
        String sendMessageCode = this.getValideCode(user.getEmail());
        if (!sendMessageCode.equals(userUpdatePasswordRequest.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 更新密码
        String userPassword = userUpdatePasswordRequest.getUserPassword();
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        user.setUserPassword(encryptPassword);
        return this.updateById(user);
    }

    /**
     * 根据用户名获取用户
     *
     * @param userAccount
     * @return
     */
    @Override
    public User getByUserAccount(String userAccount) {
        ThrowUtils.throwIf(StringUtils.isAnyBlank(userAccount), ErrorCode.PARAMS_ERROR);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        return this.getOne(queryWrapper);
    }

    /**
     * 获取验证码
     *
     * @param email
     * @return
     */
    @Override
    public String getValideCode(String email) {
        Pattern emailValidPattern = Pattern.compile("[a-zA-Z0-9]+@[A-Za-z0-9]+\\.[a-z0-9]");
        Matcher emailMatch = emailValidPattern.matcher(email);
        if (!emailMatch.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        // 获取验证码
        String redisKey = String.format(SEND_EMAIL_KEY + email);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        UserSendEmail sendMessage = (UserSendEmail) valueOperations.get(redisKey);
        if (!Optional.ofNullable(sendMessage).isPresent()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取验证码失败!");
        }
        String code = sendMessage.getCode();
        valueOperations.getOperations().delete(redisKey);
        return code;

    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




