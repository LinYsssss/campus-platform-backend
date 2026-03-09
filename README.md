# 一体化智慧校园系统 (Integrated Smart Campus System) - Backend

## 1. 项目概述 (Project Overview)
“一体化智慧校园系统”是一个致力于解决传统校园管理中数据孤岛、业务流程协同效率低等痛点的综合性平台。系统整合了教学执行、学生后勤服务及系统底层运维体系，为师生提供便捷化的一站式线上办事途径，同时为管理者提供高效互通的数据化运营手段。

本项目为系统的**后端（Backend）**代码仓库。

## 2. 核心技术栈 (Tech Stack)
- **核心框架**: Java 17 + Spring Boot 3.x (基于 Jakarta EE规范)
- **数据持久层**: MySQL 8.0.32 + MyBatis-Plus + HikariCP
- **安全与权限控制**: Sa-Token体系 (支持路由拦截、细粒度注解鉴权)
- **缓存层**: Redis 7.x + JetCache (本地 Caffeine + 远端 Redis 双层缓存隔离架构)
- **开发效率包**: Hutool (用于 BCrypt 散列加密、验证码生成、全局 Excel 解析导入等)

## 3. 系统模块划分 (Modules)
项目采用按层与按模块混合切割的 MVC 结构，核心根包为 `com.campus.system`：
- **公共基座 (`common`, `config`, `util`)**: 统一异常处理 (`Result<T>`)、Web 拦截器 (`SaTokenConfig`)、MyBatis-Plus 分页与自动填充、JetCache 本地+Redis缓存拦截、以及常用工具类（如 `SecurityUtils`, `FileUtils`）。
- **底层管控组 (`modules/sys`)**: 包含 `sys_user`, `sys_role`, `sys_menu`, `sys_dict`, `sys_log` 等9张核心表。负责后台账户、动态菜单权限分发、字典解析与操作日志防爆破审计。
- **教研链服务组 (`modules/edu`)**: 包含 `edu_course`, `edu_timetable`, `edu_attendance`, `edu_score` 等11张表。涵盖课程大纲、智能排课课表、时间窗防作弊签到、成绩录入流转与申诉复议机。
- **后勤综合服务组 (`modules/svc`)**: 包含 `campus_notice`, `campus_dormitory`, `campus_repair`, `campus_card`, `campus_book` 等11张表。接管通告下发、宿舍分配入住管理、大单图纸举证报修流以及图书/消费流水系统。

> **注**：以上各模块的 Entity, Mapper, Service, Controller 已由 MyBatis-Plus Code Generator 基于底表 DDL 物理模型全量自动生成。采用 `MyBatis-Plus` 的 `IService` 和 `BaseMapper` 提供丰富的单表 CRUD 及 Lambda 链式调用能力。

## 4. 接口规范与开发细则
- **统一响应外包**: 全路网采用标准的 `Result<T>` `(code, msg, data)` 回应 JSON 报文。
- **字段准入标尺**: 表必须含有主键（自增 `id`）、`create_time`、`update_time` 与逻辑删除屏障 `is_deleted`。
- **去物理外键化**: 抛弃物理级外键死锁约束，全面依仗 Service 层与业务逻辑事务去管理防抛残缺垃圾脏数据现象。
- **异常收口网**: 应用全局 `@RestControllerAdvice` ，实现 `BusinessException` 的友好翻译反馈屏蔽，严防前端白屏暴漏服务器具体文件堆栈。

## 5. 快速启动 (Quick Start)
1. 准备环境：配置好 JDK 17, 部署好 MySQL 8.x 和 Redis 7.x 实例。
2. 数据库初始化：在 MySQL 中执行预置好的 `campus_system` DDL/DML 脚本。
3. 修改配置：在 `application.yml` 里填入您的数据库及 Redis 账号密码及连接字符串。
4. 运行：启动 `CampusSystemApplication.java` 即刻拉起服务。默认端口及路由请参考配置设定。

## 6. 构建与部署 (Build & Deploy)
采用 Maven Lifecycle 命令打出包含全部环境与依赖关系的 `Fat-Jar`：
```bash
mvn clean package -DskipTests
```
将生成品包放入拥有 JDK 17 及所需配置挂载环境的服务器端上，指令启动执行即可跑通运转。

---
*本项目严格按照《一体化智慧校园系统需求说明书》与《后端详细开发方案》推进实施编著，开发周期为8周。*
