package com.campus.system.modules.auth.controller;

import com.campus.system.common.api.Result;
import com.campus.system.modules.auth.dto.LoginDTO;
import com.campus.system.modules.auth.dto.RegisterDTO;
import com.campus.system.modules.auth.service.AuthService;
import com.campus.system.modules.auth.vo.CaptchaVO;
import com.campus.system.modules.auth.vo.LoginVO;
import com.campus.system.modules.auth.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证授权控制器。
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出与验证码相关接口")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码", description = "生成登录所需的图形验证码与验证码标识")
    public Result<CaptchaVO> getCaptcha() {
        return Result.success(authService.generateCaptcha());
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "校验用户名、密码和验证码，登录成功后返回令牌信息")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "注销当前登录会话")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "开放注册新账号，校验验证码后创建用户")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success();
    }

    @GetMapping("/userInfo")
    @Operation(summary = "获取当前登录用户信息", description = "返回用户详情、权限列表与角色列表")
    public Result<UserInfoVO> userInfo() {
        return Result.success(authService.getUserInfo());
    }
}
