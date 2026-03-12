# 一体化智慧校园系统 (Integrated Smart Campus System) - Backend

## 1. 项目概述
"一体化智慧校园系统"是一个致力于解决传统校园管理中数据孤岛、业务流程协同效率低等痛点的综合性平台。系统整合了教学执行、学生后勤服务及系统底层运维体系，为师生提供便捷化的一站式线上办事途径，同时为管理者提供高效互通的数据化运营手段。

本项目为系统的**后端（Backend）**代码仓库。

## 2. 核心技术栈

| 层次 | 技术选型 |
|---|---|
| 核心框架 | Java 17 + Spring Boot 3.2.4 (Jakarta EE) |
| 数据持久 | MySQL 8.0 + MyBatis-Plus 3.5.5 + HikariCP |
| 安全认证 | Sa-Token 1.37.0 (路由拦截 + 注解鉴权 + RBAC) |
| 缓存架构 | Redis 7.x + JetCache 2.7.3 (Caffeine + Redis 双层缓存) |
| API 文档 | Knife4j 4.3.0 (OpenAPI 3.0) |
| 工具库 | Hutool 5.8.26 (BCrypt / CAPTCHA / Excel / 文件处理) |
| 构建工具 | Maven (多模块: campus-common + campus-server) |

## 3. 项目结构

```
campusPlatform/
├── campus-common/          # 公共基座模块
│   └── com.campus.system.common
│       ├── api/            # Result<T>, PageResult 统一响应
│       ├── entity/         # BaseEntity 公共字段
│       └── exception/      # BusinessException
├── campus-server/          # 业务主程序
│   └── com.campus.system
│       ├── annotation/     # @LogRecord 操作日志注解 + AOP切面
│       ├── config/         # SaTokenConfig, CorsConfig, SwaggerConfig, MyBatisPlusConfig
│       ├── modules/
│       │   ├── auth/       # 认证模块 (登录/验证码/RBAC)
│       │   ├── sys/        # 系统管理 (用户/角色/菜单/字典/日志)
│       │   ├── edu/        # 教研管理 (课程/排课/课件/评价/考勤/请假/成绩/申诉)
│       │   └── svc/        # 后勤服务 (通知/宿舍/报修/校园卡/图书/仪表盘)
│       └── CampusSystemApplication.java
└── sql/                    # DDL/DML 初始化脚本 (31张表)
```

## 4. API 接口总览

### 4.1 认证模块 (`/auth`)
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/auth/captcha` | 获取图形验证码 |
| POST | `/auth/login` | 用户登录 |
| POST | `/auth/logout` | 退出登录 |

### 4.2 系统管理 (`/sys`)
| 方法 | 路径 | 说明 |
|---|---|---|
| GET/POST/PUT/DELETE | `/sys/user/**` | 用户CRUD + 状态切换 + 密码重置 + Excel导入导出 |
| GET/POST/PUT/DELETE | `/sys/role/**` | 角色CRUD + 权限菜单分配 |
| GET/POST/PUT/DELETE | `/sys/menu/**` | 菜单CRUD + 树形结构查询 |
| GET/POST/PUT/DELETE | `/sys/dict/**` | 字典类型与数据管理 |
| GET | `/sys/log/**` | 操作日志 + 登录日志分页查询 |

### 4.3 教研管理 (`/edu`)
| 方法 | 路径 | 说明 |
|---|---|---|
| GET/POST/PUT/DELETE | `/edu/course/**` | 课程CRUD + 教师/班级绑定 + 结课操作 |
| GET/POST/PUT/DELETE | `/edu/timetable/**` | 排课管理 + 我的课表 + 班级课表 |
| GET/POST/DELETE | `/edu/material/**` | 课件上传(MD5去重) + 下载(计数) + 删除 |
| GET/POST/DELETE | `/edu/evaluation/**` | 课程评价(每人限一次) |
| GET/POST/PUT | `/edu/attendance/**` | 🔥 Redis签到(6位码+TTL) + 签到统计 |
| GET/POST/PUT | `/edu/leave/**` | 请假提交 + 审批(通过/驳回) |
| GET/POST/PUT | `/edu/score/**` | 🔒 成绩录入(防篡改) + 审核 + 申诉 |

### 4.4 后勤服务 (`/svc`)
| 方法 | 路径 | 说明 |
|---|---|---|
| GET/POST/PUT/DELETE | `/svc/notice/**` | 公告(草稿/发布) + 自动已读标记 |
| GET/POST/PUT/DELETE | `/svc/dorm/**` | 楼栋/房间/入住分配(床位计数同步) |
| GET/POST/PUT | `/svc/repair/**` | 🔧 报修四阶段(提交→受理→完成→验收) |
| GET/POST/PUT | `/svc/card/**` | 校园卡消费记录 + 充值 + 挂失/解挂 |
| GET/POST/PUT/DELETE | `/svc/book/**` | 📚 图书CRUD + 借书(库存扣减) + 还书(逾期计算) |
| GET/POST | `/dashboard/**` | 首页仪表盘实时统计 + 快照 |

## 5. 核心设计亮点

- **AOP 操作审计**：`@LogRecord` 注解 + 异步切面，自动捕获操作人/IP/入参/结果/耗时
- **Redis Cache-Aside 签到**：教师发起→生成6位签到码→写入Redis(TTL=签到时长)→学生命中缓存毫秒级签到
- **成绩防篡改**：status=2(已归档)状态锁死修改入口，申诉受理后自动解锁
- **文件上传安全**：白名单格式(12种) + 50MB限制 + MD5去重 + 物理删除同步
- **统一响应封装**：`Result<T>` + `PageResult<T>` + 全局异常拦截 `@RestControllerAdvice`
- **RBAC 权限体系**：Sa-Token 路由拦截 + `@SaCheckPermission` / `@SaCheckRole` 细粒度控制

## 6. 快速启动

```bash
# 1. 环境要求：JDK 17, MySQL 8.x, Redis 7.x

# 2. 数据库初始化
mysql -u root -p < sql/campus_system_campus.sql

# 3. 修改配置 (数据库/Redis连接信息)
vim campus-server/src/main/resources/application.yml

# 4. 编译运行
mvn clean compile -DskipTests
cd campus-server && mvn spring-boot:run

# 5. 访问 API 文档
# http://localhost:8080/doc.html
```

## 7. 构建部署

```bash
mvn clean package -DskipTests
java -jar campus-server/target/campus-server-1.0.0-SNAPSHOT.jar
```

---
*本项目严格按照《一体化智慧校园系统需求说明书》与《后端详细开发方案》推进实施，覆盖 sys/edu/svc 三大业务域共31张数据表、22个业务控制器。*
