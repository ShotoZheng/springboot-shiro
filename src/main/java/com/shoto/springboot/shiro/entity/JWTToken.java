package com.shoto.springboot.shiro.entity;

import lombok.AllArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@AllArgsConstructor
public class JWTToken implements AuthenticationToken {

    private String token;

    @Override
    public Object getPrincipal() {
        return this.token;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }
}
