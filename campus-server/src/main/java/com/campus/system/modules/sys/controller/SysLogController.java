package com.campus.system.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.modules.sys.entity.SysLoginLog;
import com.campus.system.modules.sys.entity.SysOperateLog;
import com.campus.system.modules.sys.service.ISysLoginLogService;
import com.campus.system.modules.sys.service.ISysOperateLogService;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统日志查询控制器（操作日志 + 登录日志）
 */
@RestController
@RequestMapping("/sys/log")
@RequiredArgsConstructor
public class SysLogController {

    private final ISysOperateLogService operateLogService;
    private final ISysLoginLogService loginLogService;

    // ============ 操作日志 ============

    /**
     * 分页查询操作日志
     * GET /api/sys/log/operate
     */
    @GetMapping("/operate")
    @SaCheckPermission("sys:log:list")
    public Result<PageResult<SysOperateLog>> operatePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operateType) {

        LambdaQueryWrapper<SysOperateLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(module)) {
            wrapper.like(SysOperateLog::getModule, module);
        }
        if (StrUtil.isNotBlank(operateType)) {
            wrapper.eq(SysOperateLog::getOperateType, operateType);
        }
        wrapper.orderByDesc(SysOperateLog::getId);

        Page<SysOperateLog> page = operateLogService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }

    // ============ 登录日志 ============

    /**
     * 分页查询登录日志
     * GET /api/sys/log/login
     */
    @GetMapping("/login")
    @SaCheckPermission("sys:log:list")
    public Result<PageResult<SysLoginLog>> loginPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String username) {

        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(SysLoginLog::getUsername, username);
        }
        wrapper.orderByDesc(SysLoginLog::getId);

        Page<SysLoginLog> page = loginLogService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(new PageResult<>(page.getTotal(), page.getRecords(), (long) pageNum, (long) pageSize));
    }
}
