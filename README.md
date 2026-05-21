# 家用备件库存统计

一个基于 Spring Boot 的家庭备件库存管理系统，支持多用户、入库/消耗记录追踪、低库存预警。

## 技术栈

- **后端**: Java 17, Spring Boot 3.2.5, Spring Data JPA
- **数据库**: MySQL 8.0
- **前端**: 原生 HTML/CSS/JS（单页应用，JWT 鉴权）
- **部署**: Docker / Docker Compose

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 本地开发

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE spare_parts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 2. 修改数据库连接配置（如需要）
# 编辑 src/main/resources/application.yml

# 3. 构建
mvn clean package -DskipTests

# 4. 启动
java -jar target/spare-parts-1.0.0.jar
```

访问 `http://localhost:8080`

### Docker 部署

```bash
# 构建镜像
mvn clean package -DskipTests
docker build -t spare-parts:1.0.0 .

# 启动（需要本地 MySQL 运行中）
docker-compose up -d
```

## 功能

- 备件库存管理：新增、入库补充、消耗记录、删除
- 单位选择：支持个/盒/支/包/瓶/卷/对/套/米/升等量词
- 低库存预警：库存 ≤ 2 时自动标记
- 操作历史：完整的入库/消耗记录追踪
- 多用户：JWT 登录鉴权，数据按用户隔离
- 微信登录：支持微信 OAuth 扫码登录

## 数据库表

| 表名 | 说明 |
|------|------|
| `users` | 用户表 |
| `parts` | 备件库存表（按 user_id 隔离） |
| `history_records` | 入库/消耗操作记录 |

## 项目结构

```
src/main/java/com/example/spareparts/
├── SparePartsApplication.java    # 入口
├── config/                       # 配置（CORS、JWT、安全）
├── controller/                   # 控制器
│   ├── AuthController.java       # 登录/注册
│   ├── InventoryController.java  # 库存 CRUD
│   └── WechatController.java     # 微信登录
├── filter/                       # JWT 鉴权过滤器
├── model/                        # 实体（User, Part, HistoryRecord）
├── repository/                   # JPA Repository
└── service/                      # 业务逻辑
```

## 服务器部署

部署目标：`192.168.188.200`（CentOS 7）

详见 [部署流程文档](#) 或参考 `docker-compose.yml` 和 `Dockerfile`。
