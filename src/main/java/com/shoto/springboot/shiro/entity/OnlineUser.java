package com.shoto.springboot.shiro.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUser {
    // session id
    private String sessionId;
    // 用户id
    private String userId;
    // 用户名称
    private String username;
    // 用户主机地址
    private String host;
    // 用户登录时系统IP
    private String systemHost;
    // 状态
    private String status;
    // session创建时间
    private Date startTimestamp;
    // session最后访问时间
    private Date lastAccessTime;
    // 超时时间
    private Long timeout;
}
