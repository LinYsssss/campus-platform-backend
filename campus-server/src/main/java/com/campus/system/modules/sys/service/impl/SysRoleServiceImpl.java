package com.campus.system.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.sys.entity.SysRole;
import com.campus.system.modules.sys.mapper.SysRoleMapper;
import com.campus.system.modules.sys.service.ISysRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
}
