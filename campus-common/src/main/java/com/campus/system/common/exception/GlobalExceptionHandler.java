package com.campus.system.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.campus.system.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常捕捉网兜
 * 拦截优先级：Sa-Token鉴权 → 参数校验 → 业务异常 → 兜底异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Sa-Token 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLoginException(NotLoginException e) {
        return Result.error(401, "Token已失效或未提供，请重新登录");
    }

    /**
     * Sa-Token 缺少权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> handleNotPermissionException(NotPermissionException e) {
        return Result.error(403, "当前账号权限不足，拒绝访问");
    }

    /**
     * Sa-Token 缺少角色异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<?> handleNotRoleException(NotRoleException e) {
        return Result.error(403, "当前账号角色不足，拒绝访问");
    }

    /**
     * 自定义业务异常拦截
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常拦截: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常拦截（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.warn("参数校验未通过: {}", message);
        return Result.error(400, message);
    }

    /**
     * 参数绑定异常拦截（@ModelAttribute）
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.warn("参数绑定异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 未捕获的系统异常兜底 —— 最后一道防线
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleSystemException(Exception e) {
        log.error("系统繁忙发生不可预知错误", e);
        return Result.error(500, "系统繁忙请求异常，请稍后再试或联系管理员");
    }
}
