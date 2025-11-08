package com.shoto.springboot.shiro.mapper;

import com.shoto.springboot.shiro.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    /**
     * 根据用户名查询所有的角色信息
     */
    List<Role> getAllRoleListByUsername(@Param("username") String username);
}
