package com.yupi.example.common.service;

import com.yupi.example.common.model.User;

import java.io.IOException;

public interface UserService {
    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user) ;
}
