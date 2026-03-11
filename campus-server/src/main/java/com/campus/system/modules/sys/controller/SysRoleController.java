package com.campus.system.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.system.common.api.Result;
import com.campus.system.common.exception.BusinessException;
import com.campus.system.modules.sys.entity.SysRole;
import com.campus.system.modules.sys.entity.SysRoleMenu;
import com.campus.system.modules.sys.mapper.SysRoleMenuMapper;
import com.campus.system.modules.sys.service.ISysRoleService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService roleService;
    private final SysRoleMenuMapper roleMenuMapper;

    /**
     * 查询全部角色列表
     */
    @GetMapping("/list")
    @SaCheckPermission("sys:role:list")
    public Result<List<SysRole>> list() {
        return Result.success(roleService.list(
                new LambdaQueryWrapper<SysRole>().orderByAsc(SysRole::getSortOrder)
        ));
    }

    /**
     * 获取角色详情（含菜单ID列表）
     */
    @GetMapping("/{id}")
    @SaCheckPermission("sys:role:query")
    public Result<RoleDetailVO> detail(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        if (role == null) throw new BusinessException("角色不存在");
        List<Long> menuIds = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id)
        ).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        RoleDetailVO vo = new RoleDetailVO();
        vo.setRole(role);
        vo.setMenuIds(menuIds);
        return Result.success(vo);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @SaCheckPermission("sys:role:add")
    public Result<Void> add(@Valid @RequestBody SysRole role) {
        long count = roleService.count(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, role.getRoleKey())
        );
        if (count > 0) throw new BusinessException("角色标识 '" + role.getRoleKey() + "' 已存在");
        roleService.save(role);
        return Result.success();
    }

    /**
     * 更新角色
     */
    @PutMapping
    @SaCheckPermission("sys:role:edit")
    public Result<Void> update(@Valid @RequestBody SysRole role) {
        roleService.updateById(role);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:role:delete")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
        return Result.success();
    }

    /**
     * 分配角色菜单权限
     * POST /api/sys/role/{id}/menus
     */
    @PostMapping("/{id}/menus")
    @SaCheckPermission("sys:role:edit")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        // 先清空
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
        // 再绑定
        if (menuIds != null) {
            menuIds.forEach(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(id);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            });
        }
        return Result.success();
    }

    /**
     * 角色详情视图对象（含菜单ID列表）
     */
    @Data
    public static class RoleDetailVO {
        private SysRole role;
        private List<Long> menuIds;
    }
}
