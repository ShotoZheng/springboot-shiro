package com.shoto.springboot.shiro.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JwtController {

    @RequestMapping("/jwtTest")
    @ResponseBody
    @RequiresPermissions("admin:list")
    public String jwtTest() {
        return "测试shiro整合jwt......";
    }
}
