package com.campus.system.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.system.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_operate_log")
public class SysOperateLog extends BaseEntity {

    /** 操作模块 */
    private String module;

    /** 操作类型（新增/修改/删除/导入/导出） */
    private String operateType;

    /** 操作人ID */
    private Long operateUserId;

    /** 操作人账号 */
    private String operateUserName;

    /** 请求方式 GET/POST/PUT/DELETE */
    private String requestMethod;

    /** 请求URL */
    private String requestUrl;

    /** 请求参数（JSON） */
    private String requestParams;

    /** 返回结果（JSON，可选截断） */
    private String responseResult;

    /** 操作IP */
    private String ip;

    /** 操作状态 0-成功 1-失败 */
    private Integer status;

    /** 错误消息 */
    private String errorMsg;

    /** 耗时（毫秒） */
    private Long costTime;
}
