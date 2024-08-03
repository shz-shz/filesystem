package com.shzshz.filesystem.service.impl;

import com.shzshz.filesystem.mapper.UserMapper;
import com.shzshz.filesystem.pojo.User;
import com.shzshz.filesystem.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public String register(User user) {
        if(!getByEmail(user.getEmail()).isEmpty()){
            return "An email address with the same name has already been registered.";
        }else {
            userMapper.add(user);
            return "OK";
        }
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    public List<User> getByEmail(String email) {
        return userMapper.getByEmail(email);
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
