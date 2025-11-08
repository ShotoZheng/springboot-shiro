package com.shoto.springboot.shiro.realm;

import com.shoto.springboot.shiro.entity.Permission;
import com.shoto.springboot.shiro.entity.Role;
import com.shoto.springboot.shiro.entity.User;
import com.shoto.springboot.shiro.mapper.PermissionMapper;
import com.shoto.springboot.shiro.mapper.RoleMapper;
import com.shoto.springboot.shiro.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 授权与认证
 */
@Slf4j
public class UserShiroAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 授权相关方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //1.获取用户名
        String username = (String) principalCollection.getPrimaryPrincipal();
        log.info("username:{}", username);

        //返回AuthorizationInfo授权类的子类
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        //2.根据用户名查询用户所有的角色信息
        List<Role> allRoleList = roleMapper.getAllRoleListByUsername(username);
        Set<String> rolesSet = new HashSet<>();
        for (Role r : allRoleList) {
            String roleName = r.getName();
            rolesSet.add(roleName);
        }
        log.info("用户：{} 拥有的角色有：{}", username, rolesSet);
        //设置用户角色信息
        simpleAuthorizationInfo.setRoles(rolesSet);

        //3.根据用户名查询用户所有的权限信息
        List<Permission> allPermissionList = permissionMapper.getAllPermissionListByUsername(username);
        Set<String> permissionSet = new HashSet<>();
        for (Permission permission : allPermissionList) {
            String permissionName = permission.getName();
            permissionSet.add(permissionName);
        }
        simpleAuthorizationInfo.setStringPermissions(permissionSet);
        log.info("用户：{} 拥有的权限有：{}", username, permissionSet);
        return simpleAuthorizationInfo;
    }

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
