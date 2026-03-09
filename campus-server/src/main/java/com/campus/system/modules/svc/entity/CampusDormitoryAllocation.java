package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 宿舍分配入住表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_dormitory_allocation")
public class CampusDormitoryAllocation extends BaseEntity {

    /** 房间ID */
    private Long roomId;

    /** 学生用户ID */
    private Long studentId;

    /** 床位号 */
    private Integer bedNumber;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 退宿日期 */
    private LocalDate checkOutDate;

    /** 状态 0-在住 1-已退宿 */
    private Integer status;
}
