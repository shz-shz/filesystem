package com.shzshz.filesystem.service;

import com.shzshz.filesystem.pojo.User;


public interface UserService {
    void register(User user);

    User getById(Integer id);

    User login(User user);

    void setKey(User user);
}
