package com.campus.system.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    /** 字典名称 */
    private String dictName;

    /** 字典类型标识（唯一） */
    private String dictType;

    /** 状态 0-正常 1-停用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
