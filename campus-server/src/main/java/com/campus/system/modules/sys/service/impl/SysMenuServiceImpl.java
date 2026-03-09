package com.campus.system.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.sys.entity.SysMenu;
import com.campus.system.modules.sys.mapper.SysMenuMapper;
import com.campus.system.modules.sys.service.ISysMenuService;
import org.springframework.stereotype.Service;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {
}
