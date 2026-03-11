package com.campus.system.modules.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.crypto.digest.BCrypt;
import com.campus.system.common.constants.SystemConstants;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.auth.dto.LoginDTO;
import com.campus.system.modules.auth.service.AuthService;
import com.campus.system.modules.auth.vo.CaptchaVO;
import com.campus.system.modules.auth.vo.LoginVO;
import com.campus.system.modules.sys.entity.SysLoginLog;
import com.campus.system.modules.sys.entity.SysUser;
import com.campus.system.modules.sys.service.ISysLoginLogService;
import com.campus.system.modules.sys.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 * 核心流程：验证码生成 → 登录校验（验证码→账号→密码→状态→防爆破）→ Token 签发
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ISysUserService userService;
    private final ISysLoginLogService loginLogService;
    private final StringRedisTemplate redisTemplate;

    /**
     * 生成图形验证码（Hutool LineCaptcha）
     * 验证码存入 Redis，有效期5分钟
     */
    @Override
    public CaptchaVO generateCaptcha() {
        // 生成验证码图片 (宽150 高48 字符数4 干扰线数20)
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(150, 48, 4, 20);
        String code = captcha.getCode().toLowerCase();
        String key = UUID.randomUUID().toString().replace("-", "");

        // 存入 Redis，5分钟过期
        redisTemplate.opsForValue().set(
                SystemConstants.CAPTCHA_PREFIX + key,
                code,
                5,
                TimeUnit.MINUTES
        );

        return new CaptchaVO(key, captcha.getImageBase64Data());
    }

    /**
     * 用户登录
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 校验验证码
        String captchaRedisKey = SystemConstants.CAPTCHA_PREFIX + dto.getCaptchaKey();
        String cachedCode = redisTemplate.opsForValue().get(captchaRedisKey);
        if (cachedCode == null) {
            throw new BusinessException("验证码已过期，请刷新后重试");
        }
        if (!cachedCode.equalsIgnoreCase(dto.getCaptchaCode())) {
            throw new BusinessException("验证码输入错误");
        }
        // 验证码一次性使用，立即删除
        redisTemplate.delete(captchaRedisKey);

        // 2. 查询用户
        SysUser user = userService.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        // 3. 检查账号状态
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new BusinessException("该账号已被停用，请联系管理员");
        }

        // 4. 防爆破：检查是否处于锁定期
        if (user.getStatus() != null && user.getStatus() == 2) {
            if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
                throw new BusinessException("账号已被锁定，请 " + SystemConstants.LOGIN_LOCK_HOURS + " 小时后再试");
            }
            // 锁定时间已过，自动解锁
            userService.update(new LambdaUpdateWrapper<SysUser>()
                    .eq(SysUser::getId, user.getId())
                    .set(SysUser::getStatus, 0)
                    .set(SysUser::getLoginFailCount, 0)
                    .set(SysUser::getLockTime, null));
            user.setStatus(0);
            user.setLoginFailCount(0);
        }

        // 5. 校验密码（BCrypt）
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            handleLoginFail(user);
            throw new BusinessException("账号或密码错误");
        }

        // 6. 登录成功，清除失败计数
        if (user.getLoginFailCount() != null && user.getLoginFailCount() > 0) {
            userService.update(new LambdaUpdateWrapper<SysUser>()
                    .eq(SysUser::getId, user.getId())
                    .set(SysUser::getLoginFailCount, 0));
        }

        // 7. Sa-Token 登录
        StpUtil.login(user.getId());

        // 8. 异步记录登录日志
        asyncRecordLoginLog(user.getId(), user.getUsername(), "登录成功");

        // 9. 组装返回
        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setUserType(user.getUserType());
        vo.setAvatar(user.getAvatar());
        return vo;
    }

    /**
     * 登出
     */
    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            StpUtil.logout();
            asyncRecordLoginLog(userId, null, "登出成功");
        }
    }

    /**
     * 处理登录失败（递增失败计数，达阈值则锁定账号）
     */
    private void handleLoginFail(SysUser user) {
        int failCount = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, user.getId())
                .set(SysUser::getLoginFailCount, failCount);

        if (failCount >= SystemConstants.LOGIN_FAIL_LOCK_COUNT) {
            // 达到阈值，锁定账号
            wrapper.set(SysUser::getStatus, 2);
            wrapper.set(SysUser::getLockTime, LocalDateTime.now().plusHours(SystemConstants.LOGIN_LOCK_HOURS));
        }
        userService.update(wrapper);
    }

    /**
     * 异步记录登录日志
     */
    @Async
    public void asyncRecordLoginLog(Long userId, String username, String message) {
        try {
            SysLoginLog log = new SysLoginLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setMsg(message);
            loginLogService.save(log);
        } catch (Exception ignored) {
            // 日志记录失败不影响主流程
        }
    }
}
