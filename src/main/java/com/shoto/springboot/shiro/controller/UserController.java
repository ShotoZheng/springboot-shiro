package com.shoto.springboot.shiro.controller;

import com.shoto.springboot.shiro.entity.User;
import com.shoto.springboot.shiro.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/queryUserByName")
    public User queryUserByName() {
        return userMapper.findUserByName("admin");
    }
}
