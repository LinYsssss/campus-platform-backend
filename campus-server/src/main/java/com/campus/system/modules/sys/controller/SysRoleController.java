package com.campus.system.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.sys.entity.SysRole;
import com.campus.system.modules.sys.entity.SysRoleMenu;
import com.campus.system.modules.sys.mapper.SysRoleMenuMapper;
import com.campus.system.modules.sys.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理控制器。
 */
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "系统角色维护接口")
public class SysRoleController {

    private final ISysRoleService roleService;
    private final SysRoleMenuMapper roleMenuMapper;

    @GetMapping("/list")
    @SaCheckPermission("sys:role:list")
    @Operation(summary = "查询角色列表")
    public Result<List<SysRole>> list() {
        return Result.success(roleService.list(
                new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getSortOrder)
        ));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:role:query")
    @Operation(summary = "获取角色详情", description = "返回角色基础信息及已绑定菜单ID")
    public Result<RoleDetailVO> detail(@Parameter(description = "角色ID") @PathVariable Long id) {
        SysRole role = roleService.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        List<Long> menuIds = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id)
        ).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        RoleDetailVO vo = new RoleDetailVO();
        vo.setRole(role);
        vo.setMenuIds(menuIds);
        return Result.success(vo);
    }

    @PostMapping
    @SaCheckPermission("sys:role:add")
    @Operation(summary = "新增角色")
    public Result<Void> add(@Valid @RequestBody SysRole role) {
        long count = roleService.count(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, role.getRoleKey())
        );
        if (count > 0) {
            throw new BusinessException("角色标识 '" + role.getRoleKey() + "' 已存在");
        }
        roleService.save(role);
        return Result.success();
    }

    @PutMapping
    @SaCheckPermission("sys:role:edit")
    @Operation(summary = "更新角色")
    public Result<Void> update(@Valid @RequestBody SysRole role) {
        roleService.updateById(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:role:delete")
    @Operation(summary = "删除角色")
    public Result<Void> delete(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRoleWithMenus(id);
        return Result.success();
    }

    @PostMapping("/{id}/menus")
    @SaCheckPermission("sys:role:edit")
    @Operation(summary = "分配角色菜单权限", description = "为角色重新绑定菜单权限")
    public Result<Void> assignMenus(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Parameter(description = "菜单ID列表") @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success();
    }

    /**
     * 角色详情。
     */
    @Data
    @Schema(name = "角色详情", description = "角色基础信息及菜单绑定结果")
    public static class RoleDetailVO {

        @Schema(description = "角色信息")
        private SysRole role;

        @Schema(description = "已绑定的菜单ID列表")
        private List<Long> menuIds;
    }
}
