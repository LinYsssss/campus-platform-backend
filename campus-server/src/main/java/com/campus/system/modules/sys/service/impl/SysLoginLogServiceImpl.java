package com.campus.system.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.sys.entity.SysLoginLog;
import com.campus.system.modules.sys.mapper.SysLoginLogMapper;
import com.campus.system.modules.sys.service.ISysLoginLogService;
import org.springframework.stereotype.Service;

@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements ISysLoginLogService {
}
