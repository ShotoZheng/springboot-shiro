package com.shoto.springboot.shiro.realm;

import com.shoto.springboot.shiro.entity.User;
import com.shoto.springboot.shiro.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UserShiroRealm extends AuthenticatingRealm {

    @Autowired
    private UserMapper userMapper;

    /**
     * 认证相关方法
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        //判断用户名, token中的用户信息是登录时候传进来的
        String username = usernamePasswordToken.getUsername();
        char[] password = usernamePasswordToken.getPassword();
        log.info("username:{}", username);
        log.info("password:{}", new String(password));

        //通过账号查找用户信息
        User user = userMapper.findUserByName(username);
        if (null == user) {
            log.error("用户不存在..");
            throw new UnknownAccountException("用户不存在！");
        }
        if ("0".equals(user.getStatus())) {
            throw new LockedAccountException("账号已被锁定,请联系管理员！");
        }

        //数据库中查询的用户名
        Object principal = user.getUsername();
        //数据库中查询的密码
        Object credentials = user.getPassword();
        String realmName = getName();
        // salt value
        ByteSource byteSource = ByteSource.Util.bytes(username);

        //判断密码
        return new SimpleAuthenticationInfo(principal, credentials, byteSource, realmName);
    }
}
