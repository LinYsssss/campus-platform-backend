package com.campus.system.common.exception;

import com.campus.system.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常捕捉网兜
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义业务异常拦截
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常拦截: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常拦截
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.warn("参数校验未通过: {}", message);
        return Result.error(400, message);
    }
    
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.warn("参数绑定异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 未捕获的系统异常兜底
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleSystemException(Exception e) {
        // 因涉及 Sa-Token 具体类，这里暂时用 Exception 泛网拦截；后续若有 Sa-Token 依赖可特化
        if (e.getClass().getName().contains("NotLoginException")) {
            return Result.error(401, "Token断供或已失效，请重新登录");
        }
        if (e.getClass().getName().contains("NotPermissionException")) {
            return Result.error(403, "当前账号权限不足，拒绝访问");
        }
        
        log.error("系统繁忙发生不可预知错误", e);
        return Result.error(500, "系统繁忙请求异常，请稍后再试或联系管理员");
    }
}
