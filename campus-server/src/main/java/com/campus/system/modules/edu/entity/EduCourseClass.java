package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程与班级关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_course_class")
public class EduCourseClass extends BaseEntity {

    /** 课程ID */
    private Long courseId;

    /** 行政班级名称 */
    private String className;
}
