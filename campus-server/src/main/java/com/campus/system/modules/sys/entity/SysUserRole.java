package com.campus.system.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户与角色关联表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {

    /** 用户ID */
    private Long userId;

    /** 角色ID */
    private Long roleId;
}
