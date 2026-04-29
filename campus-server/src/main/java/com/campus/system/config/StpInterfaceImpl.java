package com.campus.system.config;

import cn.dev33.satoken.annotation.SaCheckPermission;
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
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限/角色数据源实现
 * 实现 StpInterface 接口，由 Sa-Token 在鉴权时自动回调
 *
 * 兼容策略：当数据库尚未配置菜单权限（sys_menu/sys_role_menu 为空）时，
 * 所有已登录用户均自动拥有系统中定义的全部权限，保证系统可用。
 * 后续在后台完成菜单权限分配后，权限控制会自动按数据库配置生效。
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;
    private final RequestMappingHandlerMapping handlerMapping;

    private volatile List<String> allPermissionCache;

    /**
     * 获取用户权限列表（如 "course:add", "user:delete"）
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        long userId = Long.parseLong(loginId.toString());

        // 1. 获取用户的全部角色ID
        List<Long> roleIds = getRoleIds(userId);
        if (roleIds.isEmpty()) {
            // 用户没有任何角色时，默认给予全部权限（保证可用）
            return getAllControllerPermissions();
        }

        // 2. 获取角色关联的菜单ID
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds)
        );
        List<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        // 角色尚未绑定任何菜单时，默认给予全部权限
        if (menuIds.isEmpty()) {
            return getAllControllerPermissions();
        }

        // 3. 获取菜单上的权限标识
        List<SysMenu> menus = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .in(SysMenu::getId, menuIds)
                        .isNotNull(SysMenu::getPerms)
                        .ne(SysMenu::getPerms, "")
        );
        List<String> perms = menus.stream()
                .map(SysMenu::getPerms)
                .distinct()
                .collect(Collectors.toList());

        // 绑定的菜单都没有权限标识时，也默认给予全部权限
        if (perms.isEmpty()) {
            return getAllControllerPermissions();
        }
        return perms;
    }

    /**
     * 扫描所有 Controller 方法上的 @SaCheckPermission，收集全部权限标识
     */
    private List<String> getAllControllerPermissions() {
        if (allPermissionCache != null) {
            return allPermissionCache;
        }
        List<String> permissions = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (HandlerMethod method : handlerMethods.values()) {
            SaCheckPermission anno = method.getMethodAnnotation(SaCheckPermission.class);
            if (anno != null) {
                permissions.addAll(Arrays.asList(anno.value()));
            }
        }
        allPermissionCache = permissions.stream().distinct().collect(Collectors.toList());
        return allPermissionCache;
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
