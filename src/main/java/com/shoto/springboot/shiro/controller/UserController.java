package com.shoto.springboot.shiro.controller;

import com.shoto.springboot.shiro.entity.User;
import com.shoto.springboot.shiro.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/queryUserByName")
    public User queryUserByName() {
        return userMapper.findUserByName("admin");
    }

    @PostMapping(value = "/userLogin")
    public String toLogin(String username, String password, Model model, boolean rememberMe) {
        //1.获取Subject
        Subject subject = SecurityUtils.getSubject();
        //2.封装用户数据
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        try {
            //3.执行登录方法
            subject.login(token);
            //4.登录成功,然后跳转到success.html
            return "redirect:/success";
        } catch (UnknownAccountException e) {
            log.error("msg:该账号不存在");
            model.addAttribute("msg", "该账号不存在");
            return "index";
        } catch (IncorrectCredentialsException e) {
            log.error("msg: 密码错误，请重试");
            model.addAttribute("msg", "密码错误，请重试");
            return "index";
        } catch (LockedAccountException e) {
            log.error("msg:该账户已被锁定,请联系管理员");
            model.addAttribute("msg", "该账户已被锁定,请联系管理员");
            return "index";
        } catch (Exception e) {
            model.addAttribute("msg", "登录失败");
            log.error("msg: 登录失败");
            return "index";
        }
    }

}
