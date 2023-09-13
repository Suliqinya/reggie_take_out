package com.gk.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.reggie.entity.User;
import com.gk.reggie.mapper.UserMapper;
import com.gk.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 你是海蜇吗？
 * @version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}
