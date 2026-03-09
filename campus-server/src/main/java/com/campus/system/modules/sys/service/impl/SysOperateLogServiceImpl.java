package com.campus.system.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.sys.entity.SysOperateLog;
import com.campus.system.modules.sys.mapper.SysOperateLogMapper;
import com.campus.system.modules.sys.service.ISysOperateLogService;
import org.springframework.stereotype.Service;

@Service
public class SysOperateLogServiceImpl extends ServiceImpl<SysOperateLogMapper, SysOperateLog> implements ISysOperateLogService {
}
