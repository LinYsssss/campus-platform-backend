package com.campus.system.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.campus.system.annotation.LogRecord;
import com.campus.system.common.api.PageResult;
import com.campus.system.common.api.Result;
import com.campus.system.modules.sys.dto.SysUserCreateDTO;
import com.campus.system.modules.sys.dto.SysUserQueryDTO;
import com.campus.system.modules.sys.dto.SysUserUpdateDTO;
import com.campus.system.modules.sys.service.ISysUserService;
import com.campus.system.modules.sys.vo.SysUserVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService userService;

    /**
     * 分页查询用户列表
     * GET /api/sys/user/page
     */
    @GetMapping("/page")
    @SaCheckPermission("sys:user:list")
    public Result<PageResult<SysUserVO>> page(SysUserQueryDTO query) {
        return Result.success(userService.queryUserPage(query));
    }

    /**
     * 获取用户详情
     * GET /api/sys/user/{id}
     */
    @GetMapping("/{id}")
    @SaCheckPermission("sys:user:query")
    public Result<SysUserVO> detail(@PathVariable Long id) {
        return Result.success(userService.getUserDetail(id));
    }

    /**
     * 新增用户
     * POST /api/sys/user
     */
    @PostMapping
    @SaCheckPermission("sys:user:add")
    @LogRecord(module = "用户管理", type = "新增")
    public Result<Void> create(@Valid @RequestBody SysUserCreateDTO dto) {
        userService.createUser(dto);
        return Result.success();
    }

    /**
     * 更新用户
     * PUT /api/sys/user
     */
    @PutMapping
    @SaCheckPermission("sys:user:edit")
    @LogRecord(module = "用户管理", type = "修改")
    public Result<Void> update(@Valid @RequestBody SysUserUpdateDTO dto) {
        userService.updateUser(dto);
        return Result.success();
    }

    /**
     * 删除用户（逻辑删除）
     * DELETE /api/sys/user/{id}
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:user:delete")
    @LogRecord(module = "用户管理", type = "删除")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 切换账号状态（启用/停用）
     * PUT /api/sys/user/{id}/status/{status}
     */
    @PutMapping("/{id}/status/{status}")
    @SaCheckPermission("sys:user:edit")
    @LogRecord(module = "用户管理", type = "状态变更")
    public Result<Void> toggleStatus(@PathVariable Long id, @PathVariable Integer status) {
        userService.toggleStatus(id, status);
        return Result.success();
    }

    /**
     * 重置用户密码
     * PUT /api/sys/user/{id}/resetPwd
     */
    @PutMapping("/{id}/resetPwd")
    @SaCheckRole("admin")
    @LogRecord(module = "用户管理", type = "重置密码")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    /**
     * Excel 导入用户
     * POST /api/sys/user/import
     */
    @PostMapping("/import")
    @SaCheckRole("admin")
    @LogRecord(module = "用户管理", type = "导入")
    public Result<String> importUsers(@RequestParam("file") MultipartFile file) {
        return Result.success(userService.importUsers(file));
    }

    /**
     * Excel 导出用户
     * GET /api/sys/user/export
     */
    @GetMapping("/export")
    @SaCheckRole("admin")
    public void exportUsers(SysUserQueryDTO query, HttpServletResponse response) {
        userService.exportUsers(query, response);
    }
}
