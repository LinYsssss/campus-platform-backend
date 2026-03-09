package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统公告表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_notice")
public class CampusNotice extends BaseEntity {

    /** 公告标题 */
    private String title;

    /** 公告内容（富文本） */
    private String content;

    /** 类型 0-全体通知 1-角色定向 2-班级定向 */
    private Integer noticeType;

    /** 目标角色标识（定向时使用） */
    private String targetRole;

    /** 目标班级（定向时使用） */
    private String targetClass;

    /** 发布人ID */
    private Long publishUserId;

    /** 状态 0-草稿 1-已发布 */
    private Integer status;

    /** 发布时间 */
    private LocalDateTime publishTime;
}
