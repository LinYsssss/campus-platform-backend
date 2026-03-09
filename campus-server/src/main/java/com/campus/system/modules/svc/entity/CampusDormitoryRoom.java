package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 宿舍房间表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_dormitory_room")
public class CampusDormitoryRoom extends BaseEntity {

    /** 所属楼栋ID */
    private Long buildingId;

    /** 房间号 */
    private String roomCode;

    /** 所在楼层 */
    private Integer floor;

    /** 床位总数 */
    private Integer bedCount;

    /** 已入住数 */
    private Integer usedCount;

    /** 状态 0-正常 1-满员 2-维修中 */
    private Integer status;
}
