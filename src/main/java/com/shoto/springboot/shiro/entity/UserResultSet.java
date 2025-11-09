package com.shoto.springboot.shiro.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResultSet {
    private String code;
    private String msg;
    private String data;
}
