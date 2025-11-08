package com.shoto.springboot.shiro.resolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.Properties;

/**
 * 配置异常解析器
 */
@Configuration
public class UserAuthExceptionResolver {

    private static final String AUTHORIZATION_EXCEPTION = "AuthorizationException";

    @Bean("simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties properties = new Properties();
        //拦截到AuthorizationException就跳转到resources资源下的unauthorized.html未授权页面
        //如果带文件夹需要加上文件夹: /xxx/unauthorized.html
        properties.setProperty(AUTHORIZATION_EXCEPTION, "/unauthorized");
        resolver.setExceptionMappings(properties);
        return resolver;
    }

}
