package com.campus.system.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.sys.entity.SysUser;
import com.campus.system.modules.sys.mapper.SysUserMapper;
import com.campus.system.modules.sys.service.ISysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
}
