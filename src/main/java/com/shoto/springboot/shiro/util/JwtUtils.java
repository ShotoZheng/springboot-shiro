package com.shoto.springboot.shiro.util;

import com.shoto.springboot.shiro.config.JWTProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Autowired
    private JWTProperties jwtProperties;

    /**
     * 生成JWTToken
     *
     * @param id      用户id
     * @param subject 用户名
     * @return java.lang.String
     */
    public String createJWTToken(String id, String subject) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setId(id) //id
                .setSubject(subject) //主题
                .setIssuedAt(now) //签发时间
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret()); //加密
        //超时大于0 设置token超时
        if (jwtProperties.getTimeout() > 0) {
            //转换成超时毫秒
            long timeout = nowMillis + (jwtProperties.getTimeout() * 1000);
            builder.setExpiration(new Date(timeout));
        }
        return builder.compact();
    }

    /**
     * 解析JWT
     */
    public Claims parseJWToken(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(jwtToken)
                .getBody();
    }

}
