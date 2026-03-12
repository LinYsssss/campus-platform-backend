package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.svc.entity.CampusNoticeRead;
import com.campus.system.modules.svc.mapper.CampusNoticeReadMapper;
import com.campus.system.modules.svc.service.ICampusNoticeReadService;
import org.springframework.stereotype.Service;

@Service
public class CampusNoticeReadServiceImpl extends ServiceImpl<CampusNoticeReadMapper, CampusNoticeRead> implements ICampusNoticeReadService {
}
