package com.campus.system.modules.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.system.modules.edu.entity.EduCourse;

import java.util.List;

public interface IEduCourseService extends IService<EduCourse> {

    /**
     * 新增课程并绑定授课教师与班级。
     */
    void createCourse(EduCourse course, List<Long> teacherIds, List<String> classNames);

    /**
     * 更新课程并重新绑定授课教师与班级。
     */
    void updateCourse(EduCourse course, List<Long> teacherIds, List<String> classNames);

    /**
     * 删除课程并级联清理教师/班级绑定关系。
     */
    void deleteCourseWithBindings(Long courseId);
}
