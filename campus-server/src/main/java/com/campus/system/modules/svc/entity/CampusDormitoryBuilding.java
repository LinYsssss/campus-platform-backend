package com.campus.system.modules.svc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 宿舍楼栋表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_dormitory_building")
public class CampusDormitoryBuilding extends BaseEntity {

    /** 楼栋名称 */
    private String buildingName;

    /** 楼栋编号 */
    private String buildingCode;

    /** 楼层数 */
    private Integer floorCount;

    /** 宿管员姓名 */
    private String managerName;

    /** 宿管员电话 */
    private String managerPhone;

    /** 备注 */
    private String remark;
}
