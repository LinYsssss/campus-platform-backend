package com.campus.system.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 登录账号（学号/工号） */
    private String username;

    /** 密码（BCrypt散列） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 头像路径 */
    private String avatar;

    /** 性别 0-未知 1-男 2-女 */
    private Integer gender;

    /** 手机号码 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 所属院系/部门 */
    private String deptName;

    /** 所属班级（学生专属） */
    private String className;

    /** 用户类型 0-学生 1-教师 2-管理员 */
    private Integer userType;

    /** 账号状态 0-正常 1-停用 2-锁定 */
    private Integer status;

    /** 连续登录失败次数 */
    private Integer loginFailCount;

    /** 账号锁定截止时间 */
    private LocalDateTime lockTime;

    /** 备注 */
    private String remark;
}
