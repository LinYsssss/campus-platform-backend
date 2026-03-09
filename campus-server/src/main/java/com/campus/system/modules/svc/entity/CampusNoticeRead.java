package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公告已读状态表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_notice_read")
public class CampusNoticeRead extends BaseEntity {

    /** 公告ID */
    private Long noticeId;

    /** 用户ID */
    private Long userId;

    /** 阅读时间 */
    private LocalDateTime readTime;
}
