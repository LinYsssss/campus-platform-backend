package com.campus.system.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 父菜单ID（0为顶层） */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 类型 M-目录 C-菜单 F-按钮 */
    private String menuType;

    /** 路由地址 */
    private String path;

    /** 前端组件路径 */
    private String component;

    /** 权限标识（如 course:add） */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 显示排序 */
    private Integer sortOrder;

    /** 是否可见 0-显示 1-隐藏 */
    private Integer visible;

    /** 状态 0-正常 1-停用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
