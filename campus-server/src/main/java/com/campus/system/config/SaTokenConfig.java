package com.campus.system.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 路由拦截器配置
 * 统一鉴权入口：白名单路径放行，其余全部登录校验
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 全局登录校验（排除白名单）
            SaRouter.match("/**")
                    .notMatch(
                            // 认证模块（验证码、登录、注册）— 兼容有/无前缀两种情况
                            "/auth/captcha", "/api/auth/captcha",
                            "/auth/login", "/api/auth/login",
                            "/auth/register", "/api/auth/register",
                            // Swagger / Knife4j 文档
                            "/doc.html",
                            "/webjars/**",
                            "/swagger-resources/**",
                            "/v3/api-docs/**",
                            // 静态资源
                            "/favicon.ico",
                            "/error"
                    )
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
