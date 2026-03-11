package com.campus.system.modules.sys.vo;

import lombok.Data;
import java.util.List;

/**
 * 菜单树形节点视图对象
 */
@Data
public class MenuTreeVO {

    private Long id;
    private Long parentId;
    private String menuName;
    private String menuType;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sortOrder;
    private Integer visible;
    private Integer status;

    /** 子节点 */
    private List<MenuTreeVO> children;
}
