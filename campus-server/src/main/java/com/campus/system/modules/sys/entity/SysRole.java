package com.campus.system.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色名称 */
    private String roleName;

    /** 角色标识（如 admin, teacher, student） */
    private String roleKey;

    /** 显示排序 */
    private Integer sortOrder;

    /** 状态 0-正常 1-停用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
