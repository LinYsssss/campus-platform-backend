package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.svc.entity.CampusNotice;
import com.campus.system.modules.svc.mapper.CampusNoticeMapper;
import com.campus.system.modules.svc.service.ICampusNoticeService;
import org.springframework.stereotype.Service;

@Service
public class CampusNoticeServiceImpl extends ServiceImpl<CampusNoticeMapper, CampusNotice> implements ICampusNoticeService {
}
