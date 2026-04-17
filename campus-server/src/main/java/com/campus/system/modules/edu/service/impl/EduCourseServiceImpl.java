package com.campus.system.modules.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.system.modules.edu.entity.EduCourse;
import com.campus.system.modules.edu.entity.EduCourseClass;
import com.campus.system.modules.edu.entity.EduCourseTeacher;
import com.campus.system.modules.edu.mapper.EduCourseClassMapper;
import com.campus.system.modules.edu.mapper.EduCourseMapper;
import com.campus.system.modules.edu.mapper.EduCourseTeacherMapper;
import com.campus.system.modules.edu.service.IEduCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements IEduCourseService {

    private final EduCourseTeacherMapper courseTeacherMapper;
    private final EduCourseClassMapper courseClassMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCourse(EduCourse course, List<Long> teacherIds, List<String> classNames) {
        this.save(course);
        bindTeachers(course.getId(), teacherIds);
        bindClasses(course.getId(), classNames);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCourse(EduCourse course, List<Long> teacherIds, List<String> classNames) {
        this.updateById(course);
        courseTeacherMapper.delete(new LambdaQueryWrapper<EduCourseTeacher>().eq(EduCourseTeacher::getCourseId, course.getId()));
        courseClassMapper.delete(new LambdaQueryWrapper<EduCourseClass>().eq(EduCourseClass::getCourseId, course.getId()));
        bindTeachers(course.getId(), teacherIds);
        bindClasses(course.getId(), classNames);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourseWithBindings(Long courseId) {
        courseTeacherMapper.delete(new LambdaQueryWrapper<EduCourseTeacher>().eq(EduCourseTeacher::getCourseId, courseId));
        courseClassMapper.delete(new LambdaQueryWrapper<EduCourseClass>().eq(EduCourseClass::getCourseId, courseId));
        this.removeById(courseId);
    }

    private void bindTeachers(Long courseId, List<Long> teacherIds) {
        if (teacherIds == null) return;
        teacherIds.forEach(tid -> {
            EduCourseTeacher ct = new EduCourseTeacher();
            ct.setCourseId(courseId);
            ct.setTeacherId(tid);
            courseTeacherMapper.insert(ct);
        });
    }

    private void bindClasses(Long courseId, List<String> classNames) {
        if (classNames == null) return;
        classNames.forEach(cn -> {
            EduCourseClass cc = new EduCourseClass();
            cc.setCourseId(courseId);
            cc.setClassName(cn);
            courseClassMapper.insert(cc);
        });
    }
}
