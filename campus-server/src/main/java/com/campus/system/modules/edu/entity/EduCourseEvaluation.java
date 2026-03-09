package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程评价表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_course_evaluation")
public class EduCourseEvaluation extends BaseEntity {

    /** 课程ID */
    private Long courseId;

    /** 评价学生ID */
    private Long studentId;

    /** 星级评分 1-5 */
    private Integer starRating;

    /** 文字评价（上限200字） */
    private String content;
}
