package com.shoto.springboot.shiro.config;

import com.shoto.springboot.shiro.filter.UserJWTFilter;
import com.shoto.springboot.shiro.listener.UserShiroSessionListener;
import com.shoto.springboot.shiro.realm.UserJWTShiroAuthorizingRealm;
import com.shoto.springboot.shiro.realm.UserShiroAuthenticatingRealm;
import com.shoto.springboot.shiro.realm.UserShiroAuthorizingRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

    @Autowired
    private JWTProperties jwtProperties;

    /**
     * 将Realm注册到securityManager中
     */
//    @Bean("securityManager")
//    public DefaultWebSecurityManager securityManager() {
//        UserShiroAuthenticatingRealm userShiroAuthenticatingRealm = userShiroAuthenticatingRealm(hashedCredentialsMatcher());
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(userShiroAuthenticatingRealm);
//        securityManager.setRememberMeManager(rememberMeManager());
//        return securityManager;
//    }

    @Bean
    public UserJWTShiroAuthorizingRealm userJWTShiroAuthorizingRealm() {
        UserJWTShiroAuthorizingRealm userJWTShiroAuthorizingRealm = new UserJWTShiroAuthorizingRealm();
        userJWTShiroAuthorizingRealm.setCredentialsMatcher(new SimpleCredentialsMatcher());
        return userJWTShiroAuthorizingRealm;
    }

    /**
     * 配置自定义认证Realm
     */
    @Bean
    public UserShiroAuthenticatingRealm userShiroAuthenticatingRealm(HashedCredentialsMatcher matcher) {
        UserShiroAuthenticatingRealm userShiroAuthenticatingRealm = new UserShiroAuthenticatingRealm();
        userShiroAuthenticatingRealm.setCredentialsMatcher(matcher);
        return userShiroAuthenticatingRealm;
    }


    /**
     * 配置自定义授权Realm
     */
    @Bean
    public UserShiroAuthorizingRealm userShiroAuthorizingRealm(HashedCredentialsMatcher matcher) {
        UserShiroAuthorizingRealm userShiroAuthorizingRealm = new UserShiroAuthorizingRealm();
        userShiroAuthorizingRealm.setCredentialsMatcher(matcher);
        return userShiroAuthorizingRealm;
    }

    /**
     * 开启Shiro注解配置
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 认证
     */
    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager() {
        // 设置多个Realm
        List<Realm> realms = new ArrayList<>();
        realms.add(userShiroAuthorizingRealm(hashedCredentialsMatcher()));
        realms.add(userJWTShiroAuthorizingRealm());
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(realms);

        // 设置记住我
        securityManager.setRememberMeManager(rememberMeManager());
        // 设置缓存
        securityManager.setCacheManager(redisCacheManager());
        //设置会话管理器
//        securityManager.setSessionManager(sessionManager());
        /*
         * 关闭shiro自带的session
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        return securityManager;
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

        // 添加自己的自定义jwt过滤器，并取名为jwt
        LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();
        filtersMap.put("jwt", new UserJWTFilter(jwtProperties));
        shiroFilterFactoryBean.setFilters(filtersMap);

        shiroFilterFactoryBean.setLoginUrl("/index");
        shiroFilterFactoryBean.setSuccessUrl("/success");
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        Map<String, String> filterChainDefinitionMap = getFilterChainDefinitionMap();

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    private Map<String, String> getFilterChainDefinitionMap() {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 1. 首先配置匿名访问的路径
        filterChainDefinitionMap.put("/index", "anon");
        filterChainDefinitionMap.put("/userLogin", "anon");
        filterChainDefinitionMap.put("/unauthorized", "anon"); // 确保未授权页面可以访问
        filterChainDefinitionMap.put("/forceLogout", "anon"); // 踢人

        // 2. 然后配置需要特定角色/权限的路径
        filterChainDefinitionMap.put("/admin", "authc, roles[admin]"); // 同时需要认证和角色
        filterChainDefinitionMap.put("/user", "authc, roles[user]");

        // 3. 配置记住我功能的路径
        filterChainDefinitionMap.put("/remember", "user");

        // 4. 最后配置通用认证规则
//        filterChainDefinitionMap.put("/**", "authc");
        // 自定义 jwt 认证
        filterChainDefinitionMap.put("/**", "jwt");
        return filterChainDefinitionMap;
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

    /**
     * 配置Redis缓存管理器
     */
    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        //设置redis管理器
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    /**
     * 配置redis管理器
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        //设置一小时超时，单位是秒
        redisManager.setExpire(3600);
        return redisManager;
    }

    /**
     * 注册RedisSessionDAO
     */
    @Bean
    public SessionDAO sessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /**
     * 注册SessionManager会话管理器
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        List<SessionListener> listeners = new ArrayList<>();
        //添加自己实现的会话监听器
        listeners.add(new UserShiroSessionListener());
        //添加会话监听器给sessionManager管理
        sessionManager.setSessionListeners(listeners);
        //添加SessionDAO给sessionManager管理
        sessionManager.setSessionDAO(sessionDAO());
        //设置全局(项目)session超时单位 毫秒   -1为永不超时
        sessionManager.setGlobalSessionTimeout(360000);
        return sessionManager;
    }
}
