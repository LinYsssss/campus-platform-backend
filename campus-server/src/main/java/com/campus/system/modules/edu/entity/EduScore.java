package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_score")
public class EduScore extends BaseEntity {

    /** 课程ID */
    private Long courseId;

    /** 学生ID */
    private Long studentId;

    /** 录入教师ID */
    private Long teacherId;

    /** 分数 */
    private BigDecimal score;

    /** 评分制 0-百分制 1-等级制 */
    private Integer scoreType;

    /** 等级（A/B/C/D/F，等级制时使用） */
    private String scoreLevel;

    /** 学期 */
    private String semester;

    /** 状态 0-待审 1-已驳回 2-已归档 */
    private Integer status;

    /** 审核管理员ID */
    private Long auditUserId;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 审核/驳回意见 */
    private String auditRemark;
}
