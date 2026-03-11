package com.campus.system.modules.auth.controller;

import com.campus.system.common.api.Result;
import com.campus.system.modules.auth.dto.LoginDTO;
import com.campus.system.modules.auth.service.AuthService;
import com.campus.system.modules.auth.vo.CaptchaVO;
import com.campus.system.modules.auth.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证授权控制器
 * 白名单接口：/auth/captcha, /auth/login（在 SaTokenConfig 中已放行）
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 获取图形验证码
     * GET /api/auth/captcha
     */
    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        return Result.success(authService.generateCaptcha());
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    /**
     * 用户登出
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
