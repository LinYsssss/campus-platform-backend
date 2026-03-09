package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.svc.entity.CampusDormitoryAllocation;
import com.campus.system.modules.svc.mapper.CampusDormitoryAllocationMapper;
import com.campus.system.modules.svc.service.ICampusDormitoryAllocationService;
import org.springframework.stereotype.Service;

@Service
public class CampusDormitoryAllocationServiceImpl extends ServiceImpl<CampusDormitoryAllocationMapper, CampusDormitoryAllocation> implements ICampusDormitoryAllocationService {
}
