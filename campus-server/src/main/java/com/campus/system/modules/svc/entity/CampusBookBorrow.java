package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 图书借阅记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_book_borrow")
public class CampusBookBorrow extends BaseEntity {

    /** 图书ID */
    private Long bookId;

    /** 借阅学生ID */
    private Long studentId;

    /** 借出时间 */
    private LocalDateTime borrowTime;

    /** 应归还时间 */
    private LocalDateTime dueTime;

    /** 实际归还时间 */
    private LocalDateTime returnTime;

    /** 状态 0-借阅中 1-已归还 2-逾期 */
    private Integer status;

    /** 逾期天数 */
    private Integer overdueDays;

    /** 备注 */
    private String remark;
}
