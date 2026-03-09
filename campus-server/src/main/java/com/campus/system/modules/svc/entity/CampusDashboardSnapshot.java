package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 大屏数据快照表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_dashboard_snapshot")
public class CampusDashboardSnapshot extends BaseEntity {

    /** 快照标识（如 attendance_rate, repair_rate） */
    private String snapshotKey;

    /** 快照JSON数据 */
    private String snapshotData;

    /** 快照生成时间 */
    private LocalDateTime snapshotTime;
}
