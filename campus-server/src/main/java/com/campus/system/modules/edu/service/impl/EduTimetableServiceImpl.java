package com.campus.system.modules.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.edu.entity.EduTimetable;
import com.campus.system.modules.edu.mapper.EduTimetableMapper;
import com.campus.system.modules.edu.service.IEduTimetableService;
import org.springframework.stereotype.Service;

@Service
public class EduTimetableServiceImpl extends ServiceImpl<EduTimetableMapper, EduTimetable> implements IEduTimetableService {
}
