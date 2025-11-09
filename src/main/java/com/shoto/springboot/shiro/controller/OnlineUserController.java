package com.shoto.springboot.shiro.controller;

import com.shoto.springboot.shiro.entity.OnlineUser;
import com.shoto.springboot.shiro.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class OnlineUserController {
    @Autowired
    private OnlineUserService onlineUserService;

    @RequestMapping("/onlineUserList")
    public ModelAndView list() {
        List<OnlineUser> onlineUserList = onlineUserService.getAllOnlineUserList();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("online-user-list");
        modelAndView.addObject("onlineUserList", onlineUserList);
        return modelAndView;
    }

    @RequestMapping("/forceLogout")
    @ResponseBody
    public Map<String, String> forceLogout(@RequestParam("sessionId") String sessionId) {
        Map<String, String> resultMap = new HashMap<>(16);
        try {
            boolean forceLogout = onlineUserService.forceLogout(sessionId);
            if (forceLogout) {
                resultMap.put("code", "1");
                resultMap.put("msg", "强制踢人成功！");
            }
        } catch (Exception e) {
            log.error("forceLogout error {}", e.getMessage(), e);
            resultMap.put("code", "0");
            resultMap.put("msg", "强制踢人失败！");
        }
        return resultMap;
    }
}
