package com.shzshz.filesystem.service.impl;

import com.shzshz.filesystem.mapper.UserMapper;
import com.shzshz.filesystem.pojo.User;
import com.shzshz.filesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void register(User user) {
        userMapper.add(user);
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    public User login(User user) {
        return userMapper.getByUsernameAndPassword(user);
    }

    @Override
    public void setKey(User user) {
        userMapper.setKey(user);
    }
}
