package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 校园卡消费记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_card_record")
public class CampusCardRecord extends BaseEntity {

    /** 学生用户ID */
    private Long studentId;

    /** 校园卡号 */
    private String cardNo;

    /** 交易类型 0-消费 1-充值 */
    private Integer transactionType;

    /** 交易金额 */
    private BigDecimal amount;

    /** 余额 */
    private BigDecimal balance;

    /** 消费地点 */
    private String location;

    /** 交易时间 */
    private LocalDateTime transactionTime;

    /** 备注 */
    private String remark;
}
