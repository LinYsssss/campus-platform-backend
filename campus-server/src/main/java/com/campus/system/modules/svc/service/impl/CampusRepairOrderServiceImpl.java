package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.svc.entity.CampusRepairOrder;
import com.campus.system.modules.svc.mapper.CampusRepairOrderMapper;
import com.campus.system.modules.svc.service.ICampusRepairOrderService;
import org.springframework.stereotype.Service;

@Service
public class CampusRepairOrderServiceImpl extends ServiceImpl<CampusRepairOrderMapper, CampusRepairOrder> implements ICampusRepairOrderService {
}
