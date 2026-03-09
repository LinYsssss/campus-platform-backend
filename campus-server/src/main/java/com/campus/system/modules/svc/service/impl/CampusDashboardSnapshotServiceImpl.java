package com.campus.system.modules.svc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.svc.entity.CampusDashboardSnapshot;
import com.campus.system.modules.svc.mapper.CampusDashboardSnapshotMapper;
import com.campus.system.modules.svc.service.ICampusDashboardSnapshotService;
import org.springframework.stereotype.Service;

@Service
public class CampusDashboardSnapshotServiceImpl extends ServiceImpl<CampusDashboardSnapshotMapper, CampusDashboardSnapshot> implements ICampusDashboardSnapshotService {
}
