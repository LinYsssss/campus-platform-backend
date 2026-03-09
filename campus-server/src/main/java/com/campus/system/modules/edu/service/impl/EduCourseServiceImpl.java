package com.campus.system.modules.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.edu.entity.EduCourse;
import com.campus.system.modules.edu.mapper.EduCourseMapper;
import com.campus.system.modules.edu.service.IEduCourseService;
import org.springframework.stereotype.Service;

@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements IEduCourseService {
}
