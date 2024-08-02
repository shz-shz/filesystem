package com.shzshz.filesystem.service;

import com.shzshz.filesystem.pojo.User;

import java.util.List;


public interface UserService {
    String register(User user);

    User getById(Integer id);

    List<User> getByEmail(String email);

    User login(User user);

    void setKey(User user);
}
