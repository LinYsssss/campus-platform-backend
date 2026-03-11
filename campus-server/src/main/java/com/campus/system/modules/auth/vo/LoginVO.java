package com.campus.system.modules.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功后返回的视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /** Sa-Token 签发的 Token */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 用户类型 0-学生 1-教师 2-管理员 */
    private Integer userType;

    /** 头像 */
    private String avatar;
}
