package com.campus.system.config;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.system.modules.sys.entity.SysMenu;
import com.campus.system.modules.sys.entity.SysRole;
import com.campus.system.modules.sys.entity.SysRoleMenu;
import com.campus.system.modules.sys.entity.SysUserRole;
import com.campus.system.modules.sys.mapper.SysMenuMapper;
import com.campus.system.modules.sys.mapper.SysRoleMapper;
import com.campus.system.modules.sys.mapper.SysRoleMenuMapper;
import com.campus.system.modules.sys.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限/角色数据源实现
 * 实现 StpInterface 接口，由 Sa-Token 在鉴权时自动回调
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    /**
     * 获取用户权限列表（如 "course:add", "user:delete"）
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        long userId = Long.parseLong(loginId.toString());

        // 1. 获取用户的全部角色ID
        List<Long> roleIds = getRoleIds(userId);
        if (roleIds.isEmpty()) return new ArrayList<>();

        // 2. 获取角色关联的菜单ID
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds)
        );
        List<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).distinct().collect(Collectors.toList());
        if (menuIds.isEmpty()) return new ArrayList<>();

        // 3. 获取菜单上的权限标识
        List<SysMenu> menus = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .in(SysMenu::getId, menuIds)
                        .isNotNull(SysMenu::getPerms)
                        .ne(SysMenu::getPerms, "")
        );
        return menus.stream().map(SysMenu::getPerms).distinct().collect(Collectors.toList());
    }

    /**
     * 获取用户角色标识列表（如 "admin", "teacher", "student"）
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        long userId = Long.parseLong(loginId.toString());

        List<Long> roleIds = getRoleIds(userId);
        if (roleIds.isEmpty()) return new ArrayList<>();

        List<SysRole> roles = roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIds)
        );
        return roles.stream().map(SysRole::getRoleKey).collect(Collectors.toList());
    }

    /**
     * 获取用户的全部角色ID
     */
    private List<Long> getRoleIds(long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        return userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }
}
