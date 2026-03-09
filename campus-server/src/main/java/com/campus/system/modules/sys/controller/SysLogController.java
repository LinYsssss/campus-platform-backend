package com.campus.system.modules.sys.controller;

import com.campus.system.modules.sys.service.ISysOperateLogService;
import com.campus.system.modules.sys.service.ISysLoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志管理控制器（合并操作日志 + 登录日志）
 */
@RestController
@RequestMapping("/sys/log")
@RequiredArgsConstructor
public class SysLogController {

    private final ISysOperateLogService sysOperateLogService;
    private final ISysLoginLogService sysLoginLogService;
}
