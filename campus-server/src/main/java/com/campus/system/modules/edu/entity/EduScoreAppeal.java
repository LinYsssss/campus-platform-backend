package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 成绩申诉复议表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_score_appeal")
public class EduScoreAppeal extends BaseEntity {

    /** 成绩记录ID */
    private Long scoreId;

    /** 申诉学生ID */
    private Long studentId;

    /** 申诉理由 */
    private String reason;

    /** 佐证图片路径 */
    private String attachmentPath;

    /** 状态 0-待处理 1-已受理 2-已驳回 */
    private Integer status;

    /** 处理人ID */
    private Long handlerId;

    /** 处理时间 */
    private LocalDateTime handleTime;

    /** 处理结果 */
    private String handleResult;
}
