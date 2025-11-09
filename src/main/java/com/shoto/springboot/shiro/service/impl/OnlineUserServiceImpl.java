package com.shoto.springboot.shiro.service.impl;

import com.shoto.springboot.shiro.entity.OnlineUser;
import com.shoto.springboot.shiro.entity.User;
import com.shoto.springboot.shiro.mapper.UserMapper;
import com.shoto.springboot.shiro.service.OnlineUserService;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class OnlineUserServiceImpl implements OnlineUserService {

    /**
     * 注入会话dao
     */
    @Autowired
    private SessionDAO sessionDAO;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<OnlineUser> getAllOnlineUserList() {
        List<OnlineUser> onlineUserList = new ArrayList<>();
        //获取到当前所有有效的Session对象
        Collection<Session> activeSessions = sessionDAO.getActiveSessions();
        OnlineUser userOnline;
        //循环遍历所有有效的Session
        for (Session session : activeSessions) {
            userOnline = new OnlineUser();
            User user;
            SimplePrincipalCollection principalCollection;
            if (session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
                continue;
            } else {
                principalCollection = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                String username = (String) principalCollection.getPrimaryPrincipal();
                user = userMapper.findUserByName(username);
                userOnline.setUsername(user.getUsername());
                userOnline.setUserId(user.getId());
            }
            userOnline.setSessionId((String) session.getId());
            userOnline.setHost(session.getHost());
            userOnline.setStartTimestamp(session.getStartTimestamp());
            userOnline.setLastAccessTime(session.getLastAccessTime());
            Long timeout = session.getTimeout();
            userOnline.setStatus(timeout.equals(0L) ? "离线" : "在线");
            userOnline.setTimeout(timeout);
            onlineUserList.add(userOnline);
        }
        return onlineUserList;
    }

    @Override
    public boolean forceLogout(String sessionId) {
        Session session = sessionDAO.readSession(sessionId);
        //强制注销
        sessionDAO.delete(session);
        return true;
    }
}
