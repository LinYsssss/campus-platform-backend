package com.campus.system.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.system.common.api.Result;
import com.campus.system.modules.sys.entity.SysMenu;
import com.campus.system.modules.sys.service.ISysMenuService;
import com.campus.system.modules.sys.vo.MenuTreeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限树控制器
 */
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final ISysMenuService menuService;

    /**
     * 获取完整菜单树形结构
     * GET /api/sys/menu/tree
     */
    @GetMapping("/tree")
    @SaCheckPermission("sys:menu:list")
    public Result<List<MenuTreeVO>> tree() {
        List<SysMenu> allMenus = menuService.list(
                new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSortOrder)
        );
        List<MenuTreeVO> tree = buildTree(allMenus, 0L);
        return Result.success(tree);
    }

    /**
     * 获取菜单列表（扁平）
     */
    @GetMapping("/list")
    @SaCheckPermission("sys:menu:list")
    public Result<List<SysMenu>> list() {
        return Result.success(menuService.list(
                new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSortOrder)
        ));
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @SaCheckPermission("sys:menu:add")
    public Result<Void> add(@Valid @RequestBody SysMenu menu) {
        menuService.save(menu);
        return Result.success();
    }

    /**
     * 更新菜单
     */
    @PutMapping
    @SaCheckPermission("sys:menu:edit")
    public Result<Void> update(@Valid @RequestBody SysMenu menu) {
        menuService.updateById(menu);
        return Result.success();
    }

    /**
     * 删除菜单（含子节点检查）
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:menu:delete")
    public Result<Void> delete(@PathVariable Long id) {
        long childCount = menuService.count(
                new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id)
        );
        if (childCount > 0) {
            return Result.error("该菜单下存在子节点，请先删除子节点");
        }
        menuService.removeById(id);
        return Result.success();
    }

    // ============ 私有方法 ============

    /**
     * 递归构建菜单树
     */
    private List<MenuTreeVO> buildTree(List<SysMenu> all, Long parentId) {
        return all.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .map(m -> {
                    MenuTreeVO vo = new MenuTreeVO();
                    BeanUtil.copyProperties(m, vo);
                    vo.setChildren(buildTree(all, m.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
