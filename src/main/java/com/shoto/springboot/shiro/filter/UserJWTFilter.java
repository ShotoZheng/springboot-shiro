package com.shoto.springboot.shiro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoto.springboot.shiro.config.JWTProperties;
import com.shoto.springboot.shiro.entity.JWTToken;
import com.shoto.springboot.shiro.entity.UserResultSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义JWT过滤器
 */
@Slf4j
public class UserJWTFilter extends BasicHttpAuthenticationFilter {
    //请求头中"access_token"
    private static final String ACCESS_TOKEN = "access_token";
    //JWT属性配置信息
    private final JWTProperties jwtProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    //因为UserJWTFilter并没有注册到IOC容器中，所以不能使用@Autowired注入JWTProperties，得使用setter或者构造方法注入
    public UserJWTFilter(JWTProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 为什么最终返回的都是true，即允许访问?
     * 例如我们提供一个地址 GET /list, 登入用户和游客看到的内容是不同的， 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西
     * 所以我们在这里返回true，Controller中可以通过 subject.isAuthenticated() 来判断用户是否登入.
     * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可.
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws UnauthorizedException {
        log.info("CustomJWTFilter ---> isAccessAllowed().....");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        for (String url : jwtProperties.getNoAuthUrl().split(",")) {
            // 如果是在配置文件中配置的免认证URL，则直接返回true，表示放行
            if (pathMatcher.match(url, httpServletRequest.getRequestURI())) {
                return true;
            }
        }
        // 判断请求的请求头是否带上access_token属性
        try {
            if (isLoginAttempt(request, response)) {
                // 如果请求头中包含access_token属性，则执行executeLogin方法进行登入操作，检查access_token是否正确
                return executeLogin(request, response);
            } else {
                this.returnErrorMsg(response, "token为空");
            }
        } catch (IOException e) {
            log.error("validate token error", e);
        }
        return false;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws IOException {
        log.info("CustomJWTFilter ---> executeLogin().....");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(ACCESS_TOKEN);
        try {
            // 提交给realm进行登入，如果错误他会抛出异常并被捕获
            getSubject(request, response).login(new JWTToken(token));
            // 如果没有抛出异常则代表登入成功，返回true
            return true;
        } catch (Exception e) {
            log.error("getSubject error", e);
            this.returnErrorMsg(response, "executeLogin--->token认证失败");
            return false;
        }
    }

    /**
     * 判断用户是否想要登入。
     * 检查请求头中是否包含access_token即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        log.info("CustomJWTFilter ---> isLoginAttempt().....");
        HttpServletRequest req = (HttpServletRequest) request;
        return !StringUtils.isEmpty(req.getHeader(ACCESS_TOKEN));
    }

    /**
     * 返回自定义错误信息
     */
    private void returnErrorMsg(ServletResponse response, String msg) throws IOException {
        UserResultSet resultSet = new UserResultSet("500", msg, null);
        //响应token为空
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        //清空第一次流响应的内容
        response.resetBuffer();
        //转成json格式
        ObjectMapper object = new ObjectMapper();
        String asString = object.writeValueAsString(resultSet);
        response.getWriter().println(asString);
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        log.info("CustomJWTFilter ---> preHandle().....");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个 option请求，这里我们给 option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
