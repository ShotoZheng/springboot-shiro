package com.shoto.springboot.shiro.service;

import com.shoto.springboot.shiro.entity.OnlineUser;

import java.util.List;

public interface OnlineUserService {
    /**
     * 获取所有在线用户信息
     */
    List<OnlineUser> getAllOnlineUserList();

    /**
     * 根据sessionId强制登出
     * @param sessionId 会话ID
     */
    boolean forceLogout(String sessionId);
}
