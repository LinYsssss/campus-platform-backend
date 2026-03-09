package com.campus.system.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_login_log")
public class SysLoginLog extends BaseEntity {

    /** 用户ID */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 登录类型 0-登录 1-登出 */
    private Integer loginType;

    /** 登录状态 0-成功 1-失败 */
    private Integer status;

    /** 登录IP */
    private String ip;

    /** 浏览器UA */
    private String userAgent;

    /** 提示消息 */
    private String msg;
}
