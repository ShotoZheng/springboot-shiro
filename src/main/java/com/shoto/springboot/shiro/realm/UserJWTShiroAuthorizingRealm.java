package com.shoto.springboot.shiro.realm;

import com.shoto.springboot.shiro.entity.JWTToken;
import com.shoto.springboot.shiro.entity.User;
import com.shoto.springboot.shiro.mapper.UserMapper;
import com.shoto.springboot.shiro.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UserJWTShiroAuthorizingRealm extends BaseUserShiroAuthorizingRealm {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;

    /**
     * 支持自定义认证令牌
     * 必须重写此方法，不然Shiro会报错
     * 限定这个 Realm 只处理我们自定义的 JwtToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("UserJWTShiroAuthorizingRealm ---> doGetAuthenticationInfo()认证.....");
        // 这里的 token是从 UserJWTFilter 的 executeLogin 方法传递过来的
        String token = (String) authenticationToken.getCredentials();
        String username;
        try {
            username = jwtUtils.parseJWToken(token).getSubject();
        } catch (Exception e) {
            //抛出token认证失败
            throw new AuthenticationException("doGetAuthenticationInfo--->token认证失败");
        }
        // 通过用户名到数据库查询用户信息
        User user = userMapper.findUserByName(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在！");
        }
        return new SimpleAuthenticationInfo(username, token, getName());
    }
}
