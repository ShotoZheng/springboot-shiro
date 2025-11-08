package com.shoto.springboot.shiro.mapper;

import com.shoto.springboot.shiro.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {

    /**
     * 根据用户名查询所有的权限信息
     */
    List<Permission> getAllPermissionListByUsername(@Param("username") String username);

}
