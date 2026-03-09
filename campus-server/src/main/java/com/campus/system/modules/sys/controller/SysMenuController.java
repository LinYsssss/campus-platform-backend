package com.campus.system.modules.sys.controller;

import com.campus.system.modules.sys.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单权限管理控制器
 */
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final ISysMenuService sysMenuService;
}
