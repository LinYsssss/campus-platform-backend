package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程与教师关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_course_teacher")
public class EduCourseTeacher extends BaseEntity {

    /** 课程ID */
    private Long courseId;

    /** 教师用户ID */
    private Long teacherId;
}
