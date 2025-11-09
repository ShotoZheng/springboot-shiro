package com.shoto.springboot.shiro.controller;

import com.shoto.springboot.shiro.entity.User;
import com.shoto.springboot.shiro.entity.UserResultSet;
import com.shoto.springboot.shiro.mapper.UserMapper;
import com.shoto.springboot.shiro.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @RequestMapping("/queryUserByName")
    public User queryUserByName() {
        return userMapper.findUserByName("admin");
    }

    @PostMapping(value = "/userLogin")
    @ResponseBody
    public UserResultSet toLogin(String username, String password) {
        // 密码加密
        String md5Password = new SimpleHash("MD5", password, username, 1024).toString();
        User user = userMapper.findUserByName(username);
        if (user != null && md5Password.equals(user.getPassword())) {
            String jwtToken = jwtUtils.createJWTToken(user.getId(), user.getUsername());
            //这里只是简单的返回到前台，实际项目中这里可以将签发的JWT token设置到 HttpServletResponse 的Header中
//            ((HttpServletResponse) response).setHeader(JwtUtils.AUTH_HEADER, jwtToken);
            return new UserResultSet("200", "获取token成功", jwtToken);
        }
        return new UserResultSet("500", "用户不存在或者密码错误", null);
    }

}
