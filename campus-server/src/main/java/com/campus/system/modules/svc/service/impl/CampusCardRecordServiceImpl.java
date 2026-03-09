package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.svc.entity.CampusCardRecord;
import com.campus.system.modules.svc.mapper.CampusCardRecordMapper;
import com.campus.system.modules.svc.service.ICampusCardRecordService;
import org.springframework.stereotype.Service;

@Service
public class CampusCardRecordServiceImpl extends ServiceImpl<CampusCardRecordMapper, CampusCardRecord> implements ICampusCardRecordService {
}
