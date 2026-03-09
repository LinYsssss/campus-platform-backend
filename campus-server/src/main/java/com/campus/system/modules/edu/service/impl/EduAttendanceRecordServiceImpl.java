package com.campus.system.modules.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.edu.entity.EduAttendanceRecord;
import com.campus.system.modules.edu.mapper.EduAttendanceRecordMapper;
import com.campus.system.modules.edu.service.IEduAttendanceRecordService;
import org.springframework.stereotype.Service;

@Service
public class EduAttendanceRecordServiceImpl extends ServiceImpl<EduAttendanceRecordMapper, EduAttendanceRecord> implements IEduAttendanceRecordService {
}
