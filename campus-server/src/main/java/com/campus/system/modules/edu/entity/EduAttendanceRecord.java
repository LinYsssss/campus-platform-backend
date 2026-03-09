package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考勤签到记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_attendance_record")
public class EduAttendanceRecord extends BaseEntity {

    /** 考勤场次ID */
    private Long sessionId;

    /** 学生用户ID */
    private Long studentId;

    /** 实际签到时间 */
    private LocalDateTime signTime;

    /** 状态 0-已签到 1-缺勤 2-请假 3-补签 */
    private Integer status;

    /** 备注（补签原因等） */
    private String remark;
}
