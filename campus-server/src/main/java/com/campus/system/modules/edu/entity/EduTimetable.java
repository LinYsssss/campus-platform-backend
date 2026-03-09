package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课表排课表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_timetable")
public class EduTimetable extends BaseEntity {

    /** 课程ID */
    private Long courseId;

    /** 教师用户ID */
    private Long teacherId;

    /** 班级名称 */
    private String className;

    /** 星期几 1-7 */
    private Integer dayOfWeek;

    /** 开始节次 */
    private Integer startSection;

    /** 结束节次 */
    private Integer endSection;

    /** 教室地点 */
    private String classroom;

    /** 起始周 */
    private Integer startWeek;

    /** 结束周 */
    private Integer endWeek;

    /** 学期 */
    private String semester;
}
