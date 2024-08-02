package com.shzshz.filesystem.controller;

import com.shzshz.filesystem.pojo.Result;
import com.shzshz.filesystem.pojo.User;
import com.shzshz.filesystem.service.UserService;
import com.shzshz.filesystem.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("User Login: {}", user);
        User e = userService.login(user);
        if(e != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", e.getId());
            claims.put("email", e.getEmail());

            String jwt = JwtUtils.generateJwt(claims);

            return Result.success(jwt);
        }
        return Result.error("Error email or password");
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        log.info("User Register: {}", user);
        userService.register(user);
        return Result.success();
    }
}
