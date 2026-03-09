package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 报修工单表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_repair_order")
public class CampusRepairOrder extends BaseEntity {

    /** 工单流水号 */
    private String orderNo;

    /** 报修人ID */
    private Long applicantId;

    /** 关联房间ID */
    private Long roomId;

    /** 报修主题 */
    private String title;

    /** 问题描述 */
    private String description;

    /** 损坏照片路径（多张逗号分隔） */
    private String imagePaths;

    /** 紧急程度 0-普通 1-紧急 2-非常紧急 */
    private Integer urgencyLevel;

    /** 状态 0-待处理 1-处理中 2-已完成 3-已验收 */
    private Integer status;

    /** 处理人ID（宿管/维修工） */
    private Long handlerId;

    /** 开始处理时间 */
    private LocalDateTime handleTime;

    /** 完成时间 */
    private LocalDateTime finishTime;

    /** 完成备注 */
    private String finishRemark;

    /** 验收人ID */
    private Long verifyUserId;

    /** 验收时间 */
    private LocalDateTime verifyTime;

    /** 满意度评分 1-5 */
    private Integer verifyScore;

    /** 验收评价 */
    private String verifyRemark;
}
