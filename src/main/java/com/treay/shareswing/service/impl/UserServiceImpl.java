package com.treay.shareswing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.treay.shareswing.mapper.UserMapper;
import com.treay.shareswing.model.entity.User;
import com.treay.shareswing.service.UserService;

import org.springframework.stereotype.Service;

/**
* @author 16799
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-06-01 11:50:40
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




