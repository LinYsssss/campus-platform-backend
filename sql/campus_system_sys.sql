-- ============================================================
-- 一体化智慧校园系统 - 数据库初始化脚本 (底层管控组 sys_)
-- 数据库: campus_system | 字符集: utf8mb4 | 引擎: InnoDB
-- ============================================================

CREATE DATABASE IF NOT EXISTS `campus_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `campus_system`;

-- -----------------------------------------------------------
-- 1. 用户表 sys_user
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`    VARCHAR(50)  NOT NULL                COMMENT '登录账号（学号/工号）',
  `password`    VARCHAR(200) NOT NULL                COMMENT '密码（BCrypt散列）',
  `real_name`   VARCHAR(50)  DEFAULT NULL            COMMENT '真实姓名',
  `avatar`      VARCHAR(255) DEFAULT NULL            COMMENT '头像路径',
  `gender`      TINYINT      DEFAULT 0               COMMENT '性别 0-未知 1-男 2-女',
  `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号码',
  `email`       VARCHAR(100) DEFAULT NULL            COMMENT '邮箱',
  `dept_name`   VARCHAR(100) DEFAULT NULL            COMMENT '所属院系/部门',
  `class_name`  VARCHAR(100) DEFAULT NULL            COMMENT '所属班级（学生专属）',
  `user_type`   TINYINT      NOT NULL DEFAULT 0      COMMENT '用户类型 0-学生 1-教师 2-管理员',
  `status`      TINYINT      NOT NULL DEFAULT 0      COMMENT '账号状态 0-正常 1-停用 2-锁定',
  `login_fail_count` INT     DEFAULT 0               COMMENT '连续登录失败次数',
  `lock_time`   DATETIME     DEFAULT NULL            COMMENT '账号锁定截止时间',
  `remark`      VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- -----------------------------------------------------------
-- 2. 角色表 sys_role
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name`   VARCHAR(50)  NOT NULL                COMMENT '角色名称',
  `role_key`    VARCHAR(50)  NOT NULL                COMMENT '角色标识（如 admin, teacher, student）',
  `sort_order`  INT          DEFAULT 0               COMMENT '显示排序',
  `status`      TINYINT      NOT NULL DEFAULT 0      COMMENT '状态 0-正常 1-停用',
  `remark`      VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- -----------------------------------------------------------
-- 3. 用户-角色关联表 sys_user_role
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
  `role_id`     BIGINT       NOT NULL                COMMENT '角色ID',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';

-- -----------------------------------------------------------
-- 4. 菜单/权限表 sys_menu
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id`   BIGINT       DEFAULT 0               COMMENT '父菜单ID（0为顶层）',
  `menu_name`   VARCHAR(100) NOT NULL                COMMENT '菜单名称',
  `menu_type`   CHAR(1)      NOT NULL DEFAULT 'M'    COMMENT '类型 M-目录 C-菜单 F-按钮',
  `path`        VARCHAR(255) DEFAULT NULL            COMMENT '路由地址',
  `component`   VARCHAR(255) DEFAULT NULL            COMMENT '前端组件路径',
  `perms`       VARCHAR(200) DEFAULT NULL            COMMENT '权限标识（如 course:add）',
  `icon`        VARCHAR(100) DEFAULT NULL            COMMENT '菜单图标',
  `sort_order`  INT          DEFAULT 0               COMMENT '显示排序',
  `visible`     TINYINT      DEFAULT 0               COMMENT '是否可见 0-显示 1-隐藏',
  `status`      TINYINT      NOT NULL DEFAULT 0      COMMENT '状态 0-正常 1-停用',
  `remark`      VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- -----------------------------------------------------------
-- 5. 角色-菜单关联表 sys_role_menu
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id`     BIGINT       NOT NULL                COMMENT '角色ID',
  `menu_id`     BIGINT       NOT NULL                COMMENT '菜单ID',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

-- -----------------------------------------------------------
-- 6. 字典类型表 sys_dict_type
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_name`   VARCHAR(100) NOT NULL                COMMENT '字典名称',
  `dict_type`   VARCHAR(100) NOT NULL                COMMENT '字典类型标识（唯一）',
  `status`      TINYINT      NOT NULL DEFAULT 0      COMMENT '状态 0-正常 1-停用',
  `remark`      VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- -----------------------------------------------------------
-- 7. 字典数据表 sys_dict_data
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type`   VARCHAR(100) NOT NULL                COMMENT '所属字典类型标识',
  `dict_label`  VARCHAR(100) NOT NULL                COMMENT '字典标签',
  `dict_value`  VARCHAR(100) NOT NULL                COMMENT '字典值',
  `sort_order`  INT          DEFAULT 0               COMMENT '排序',
  `status`      TINYINT      NOT NULL DEFAULT 0      COMMENT '状态 0-正常 1-停用',
  `remark`      VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- -----------------------------------------------------------
-- 8. 操作日志表 sys_operate_log
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_operate_log`;
CREATE TABLE `sys_operate_log` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `module`        VARCHAR(50)   DEFAULT NULL            COMMENT '操作模块',
  `operate_type`  VARCHAR(50)   DEFAULT NULL            COMMENT '操作类型（新增/修改/删除/导入/导出）',
  `operate_user_id` BIGINT      DEFAULT NULL            COMMENT '操作人ID',
  `operate_user_name` VARCHAR(50) DEFAULT NULL          COMMENT '操作人账号',
  `request_method` VARCHAR(10)  DEFAULT NULL            COMMENT '请求方式 GET/POST/PUT/DELETE',
  `request_url`   VARCHAR(255)  DEFAULT NULL            COMMENT '请求URL',
  `request_params` TEXT         DEFAULT NULL            COMMENT '请求参数（JSON）',
  `response_result` TEXT        DEFAULT NULL            COMMENT '返回结果（JSON，可选截断）',
  `ip`            VARCHAR(50)   DEFAULT NULL            COMMENT '操作IP',
  `status`        TINYINT       DEFAULT 0               COMMENT '操作状态 0-成功 1-失败',
  `error_msg`     TEXT          DEFAULT NULL            COMMENT '错误消息',
  `cost_time`     BIGINT        DEFAULT 0               COMMENT '耗时（毫秒）',
  `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`    TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_operate_user` (`operate_user_id`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录表';

-- -----------------------------------------------------------
-- 9. 登录日志表 sys_login_log
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`       BIGINT        DEFAULT NULL            COMMENT '用户ID',
  `username`      VARCHAR(50)   DEFAULT NULL            COMMENT '登录账号',
  `login_type`    TINYINT       DEFAULT 0               COMMENT '登录类型 0-登录 1-登出',
  `status`        TINYINT       DEFAULT 0               COMMENT '登录状态 0-成功 1-失败',
  `ip`            VARCHAR(50)   DEFAULT NULL            COMMENT '登录IP',
  `user_agent`    VARCHAR(500)  DEFAULT NULL            COMMENT '浏览器UA',
  `msg`           VARCHAR(500)  DEFAULT NULL            COMMENT '提示消息',
  `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`    TINYINT       NOT NULL DEFAULT 0      COMMENT '逻辑删除 0-正常 1-已删除',
  PRIMARY KEY (`id`),
  INDEX `idx_username` (`username`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================================
-- 初始化种子数据 (DML)
-- ============================================================

-- 默认角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort_order`, `remark`) VALUES
(1, '超级管理员', 'admin',   1, '拥有系统最高权限'),
(2, '教师',       'teacher', 2, '一线教学任务执行者'),
(3, '学生',       'student', 3, '平台核心业务受众');

-- 默认管理员账号 (密码: admin123，BCrypt散列)
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `user_type`, `status`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 2, 0);

-- 管理员角色绑定
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 基础字典类型
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `remark`) VALUES
('用户性别', 'sys_user_gender',  '用户性别列表'),
('账号状态', 'sys_user_status',  '账号状态列表'),
('操作类型', 'sys_operate_type', '操作日志类型'),
('菜单类型', 'sys_menu_type',    '菜单类型列表');

-- 基础字典数据
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort_order`) VALUES
('sys_user_gender',  '未知', '0', 0),
('sys_user_gender',  '男',   '1', 1),
('sys_user_gender',  '女',   '2', 2),
('sys_user_status',  '正常', '0', 0),
('sys_user_status',  '停用', '1', 1),
('sys_user_status',  '锁定', '2', 2),
('sys_operate_type', '新增', '0', 0),
('sys_operate_type', '修改', '1', 1),
('sys_operate_type', '删除', '2', 2),
('sys_operate_type', '导入', '3', 3),
('sys_operate_type', '导出', '4', 4),
('sys_menu_type',    '目录', 'M', 0),
('sys_menu_type',    '菜单', 'C', 1),
('sys_menu_type',    '按钮', 'F', 2);
