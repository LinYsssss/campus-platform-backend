package com.campus.system.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.system.modules.sys.entity.SysRole;

import java.util.List;

public interface ISysRoleService extends IService<SysRole> {

    /**
     * 删除角色并清除其菜单绑定关系。
     */
    void deleteRoleWithMenus(Long roleId);

    /**
     * 为角色重新绑定菜单权限（先清空后写入）。
     */
    void assignMenus(Long roleId, List<Long> menuIds);
}
