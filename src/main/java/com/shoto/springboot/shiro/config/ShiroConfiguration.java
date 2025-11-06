package com.shoto.springboot.shiro.config;

import com.shoto.springboot.shiro.realm.UserShiroRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

    /**
     * 将Realm注册到securityManager中
     */
    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager() {
        UserShiroRealm userShiroRealm = userShiroRealm(hashedCredentialsMatcher());
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(userShiroRealm);
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    /**
     * 配置自定义的Realm
     */
    @Bean
    public UserShiroRealm userShiroRealm(HashedCredentialsMatcher matcher) {
        UserShiroRealm userShiroRealm = new UserShiroRealm();
        userShiroRealm.setCredentialsMatcher(matcher);
        return userShiroRealm;
    }

    /**
     * Shiro内置过滤器，可以实现权限相关的拦截器
     * 常用的过滤器：
     * anon: 无需认证（登录）可以访问
     * authc: 必须认证才可以访问
     * user: 如果使用rememberMe的功能可以直接访问
     * perms： 该资源必须得到资源权限才可以访问
     * role: 该资源必须得到角色权限才可以访问
     */
    @Bean
    public ShiroFilterFactoryBean userShiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //表示指定登录页面
        shiroFilterFactoryBean.setLoginUrl("/index");
        //登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/success");
        //未授权页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        //拦截器, 配置不会被拦截的链接 顺序判断
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //所有匿名用户均可访问到Controller层的该方法下
        filterChainDefinitionMap.put("/index", "anon");
        filterChainDefinitionMap.put("/userLogin", "anon");
        //user表示配置记住我或认证通过可以访问的地址
        filterChainDefinitionMap.put("/remember", "user");
        //authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * SpringShiroFilter首先注册到spring容器
     * 然后被包装成FilterRegistrationBean
     * 最后通过FilterRegistrationBean注册到servlet容器
     */
    @Bean
    public FilterRegistrationBean<DelegatingFilterProxy> delegatingFilterProxy() {
        FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean = new FilterRegistrationBean<>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("userShiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    /**
     * 密码匹配凭证管理器（密码加密需要此配置）
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //加密算法
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        // 设置加密次数
        hashedCredentialsMatcher.setHashIterations(1024);
        return hashedCredentialsMatcher;
    }

    /**
     * 设置cookie
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        //这个参数是cookie的名称,对应前端的checkbox的name=rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //如果httpOnly设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；
        simpleCookie.setHttpOnly(true);
        //记住我cookie生效时间10秒钟(单位秒)
        simpleCookie.setMaxAge(10);
        return simpleCookie;
    }

    /**
     * cookie管理对象,记住我功能
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie加密的密钥  建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }
}
