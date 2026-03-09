package com.campus.system.modules.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.edu.entity.EduAttendanceSession;
import com.campus.system.modules.edu.mapper.EduAttendanceSessionMapper;
import com.campus.system.modules.edu.service.IEduAttendanceSessionService;
import org.springframework.stereotype.Service;

@Service
public class EduAttendanceSessionServiceImpl extends ServiceImpl<EduAttendanceSessionMapper, EduAttendanceSession> implements IEduAttendanceSessionService {
}
