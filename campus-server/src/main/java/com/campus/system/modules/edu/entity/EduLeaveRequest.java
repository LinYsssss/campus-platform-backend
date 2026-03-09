package com.campus.system.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 请假申请表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_leave_request")
public class EduLeaveRequest extends BaseEntity {

    /** 申请学生ID */
    private Long studentId;

    /** 关联课程ID */
    private Long courseId;

    /** 关联考勤场次ID */
    private Long sessionId;

    /** 请假类型 0-事假 1-病假 2-其他 */
    private Integer leaveType;

    /** 请假事由 */
    private String reason;

    /** 请假开始时间 */
    private LocalDateTime startTime;

    /** 请假结束时间 */
    private LocalDateTime endTime;

    /** 附件举证图片路径 */
    private String attachmentPath;

    /** 状态 0-待审批 1-已通过 2-已驳回 */
    private Integer status;

    /** 审批人ID */
    private Long approverId;

    /** 审批时间 */
    private LocalDateTime approveTime;

    /** 审批意见 */
    private String approveRemark;
}
