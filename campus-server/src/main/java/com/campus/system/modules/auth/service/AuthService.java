package com.campus.system.modules.auth.service;

import com.campus.system.modules.auth.dto.LoginDTO;
import com.campus.system.modules.auth.vo.CaptchaVO;
import com.campus.system.modules.auth.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 生成图形验证码
     */
    CaptchaVO generateCaptcha();

    /**
     * 用户登录
     * @param dto 登录参数（账号、密码、验证码）
     * @return 登录成功后的 Token 及用户基本信息
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户登出
     */
    void logout();
}
