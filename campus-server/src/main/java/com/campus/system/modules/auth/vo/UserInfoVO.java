package com.campus.system.modules.auth.vo;

import com.campus.system.modules.sys.vo.SysUserVO;
import lombok.Data;

import java.util.List;

/**
 * 当前登录用户信息视图对象。
 */
@Data
public class UserInfoVO {

    /** 用户详情 */
    private SysUserVO user;

    /** 权限标识列表 */
    private List<String> permissions;

    /** 角色标识列表 */
    private List<String> roles;
}
