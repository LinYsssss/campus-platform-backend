package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 校园卡挂失表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_card_loss")
public class CampusCardLoss extends BaseEntity {

    /** 学生用户ID */
    private Long studentId;

    /** 校园卡号 */
    private String cardNo;

    /** 状态 0-已挂失 1-已解挂 2-已补办 */
    private Integer status;

    /** 挂失时间 */
    private LocalDateTime lossTime;

    /** 解挂时间 */
    private LocalDateTime unlockTime;

    /** 备注 */
    private String remark;
}
