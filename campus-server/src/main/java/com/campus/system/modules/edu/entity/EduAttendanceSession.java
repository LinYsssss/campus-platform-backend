package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考勤场次表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_attendance_session")
public class EduAttendanceSession extends BaseEntity {

    /** 课程ID */
    private Long courseId;

    /** 发起教师ID */
    private Long teacherId;

    /** 考勤班级 */
    private String className;

    /** 场次编码（短时缓存键） */
    private String sessionCode;

    /** 签到有效时长（分钟） */
    private Integer durationMinutes;

    /** 签到开始时间 */
    private LocalDateTime startTime;

    /** 签到截止时间 */
    private LocalDateTime endTime;

    /** 状态 0-进行中 1-已结束 */
    private Integer status;
}
