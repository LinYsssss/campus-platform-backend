package com.campus.system.annotation;

import java.lang.annotation.*;

/**
 * 操作日志记录注解
 * 标注在 Controller 方法上，由 AOP 切面自动拦截并异步落盘
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecord {

    /** 操作模块（如 "用户管理"、"课程管理"） */
    String module() default "";

    /** 操作类型（如 "新增"、"修改"、"删除"、"导入"、"导出"） */
    String type() default "";
}
