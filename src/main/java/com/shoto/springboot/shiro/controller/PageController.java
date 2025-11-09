package com.shoto.springboot.shiro.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class PageController {

    @GetMapping("/index")
    public String index() {
        //返回index.html
        return "index";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @RequestMapping("/unauthorized")
    public String unauthorized() {
        return "unauthorized";
    }

    @GetMapping("/remember")
    public String remember() {
        return "remember";
    }

    @RequiresRoles(value = "admin")
    @GetMapping("/admin")
    public String admin() {
        //返回admin.html
        return "admin";
    }

    @RequiresRoles(value = "user")
    @GetMapping("/user")
    public String user() {
        return "user";
    }

    /**
     * 使用shiro权限注解标明,只能拥有这个admin:list权限的用户访问
     */
    @RequiresPermissions(value = "admin:list")
    @RequestMapping("/adminList")
    public String adminList(){
        return "admin-list";
    }

    @RequiresPermissions(value = "user:list")
    @RequestMapping("/userList")
    public String userList(){
        return "user-list";
    }
}
