package com.shoto.springboot.shiro.realm;

import com.shoto.springboot.shiro.entity.Permission;
import com.shoto.springboot.shiro.entity.Role;
import com.shoto.springboot.shiro.mapper.PermissionMapper;
import com.shoto.springboot.shiro.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public abstract class BaseUserShiroAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 授权相关方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("BaseUserShiroAuthorizingRealm ---> doGetAuthorizationInfo()授权.....");
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
}
