package com.shoto.springboot.shiro.mapper;

import com.shoto.springboot.shiro.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名查找用户信息
     *
     * @param name 用户名
     * @return
     */
    User findUserByName(@Param("name") String name);
}
