package com.shoto.springboot.shiro.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class UserShiroSessionListener implements SessionListener {
    /**
     * 维护着个原子类型的Integer对象，用于统计在线Session的数量
     */
    private final AtomicInteger sessionCount = new AtomicInteger(0);

    @Override
    public void onStart(Session session) {
        sessionCount.getAndIncrement();
        log.info("用户登录人数增加一人{}", sessionCount.get());
    }

    @Override
    public void onStop(Session session) {
        sessionCount.decrementAndGet();
        log.info("用户登录人数减少一人{}", sessionCount.get());
    }

    @Override
    public void onExpiration(Session session) {
        sessionCount.decrementAndGet();
        log.info("用户登录过期一人{}", sessionCount.get());
    }
}
