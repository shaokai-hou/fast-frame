# Fast Frame

Fast Frame 是一个现代化的全栈后台管理系统，采用前后端分离架构，提供完整的用户管理、角色管理、菜单管理、部门管理、字典管理、配置管理、日志管理、在线用户监控、文件管理等功能模块。

## 技术栈

### 前端
- **Vue 3** - 渐进式 JavaScript 框架
- **Vite 5** - 下一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Pinia** - Vue 状态管理
- **Vue Router** - 官方路由管理器
- **Axios** - HTTP 请求库
- **SCSS** - CSS 预处理器

### 后端
- **Spring Boot 2.7** - Java Web 框架
- **MyBatis Plus** - ORM 框架
- **Sa-Token** - 轻量级权限认证框架
- **PostgreSQL** - 关系型数据库
- **Redis** - 缓存数据库
- **MinIO** - 对象存储服务

## 功能特性

- 🔐 **用户管理** - 用户增删改查、导入导出、密码重置
- 👥 **角色管理** - 角色权限配置、菜单权限分配
- 📋 **菜单管理** - 动态菜单配置、路由权限控制
- 🏢 **部门管理** - 组织架构管理、数据权限控制
- 📖 **字典管理** - 数据字典配置、字典数据维护
- ⚙️ **配置管理** - 系统参数配置
- 📝 **操作日志** - 操作记录查询
- 🔑 **登录日志** - 登录记录查询
- 🟢 **在线用户** - 实时监控在线用户、强制退出
- 📁 **文件管理** - 文件上传下载、在线预览

## 项目结构

```
fast-frame/
├── fast-web/                 # 前端项目
│   ├── src/
│   │   ├── api/              # API 接口
│   │   ├── components/       # 公共组件
│   │   ├── directives/       # 自定义指令
│   │   ├── layout/           # 布局组件
│   │   ├── router/           # 路由配置
│   │   ├── store/            # 状态管理
│   │   ├── styles/           # 样式文件
│   │   ├── utils/            # 工具函数
│   │   └── views/            # 页面组件
│   └── package.json
│
├── fast-serve/               # 后端项目
│   ├── src/main/java/com/fast/
│   │   ├── common/           # 公共模块
│   │   ├── framework/        # 框架配置
│   │   └── modules/          # 业务模块
│   │       ├── auth/         # 认证模块
│   │       ├── file/         # 文件模块
│   │       ├── home/         # 首页模块
│   │       ├── log/          # 日志模块
│   │       └── system/       # 系统模块
│   └ pom.xml
│
└── doc/                      # 文档目录
```

## 快速开始

### 环境要求

- Node.js >= 18
- pnpm >= 8
- JDK >= 11
- Maven >= 3.6
- PostgreSQL >= 14
- Redis >= 6
- MinIO (可选)

### 后端启动

1. 创建数据库
```sql
CREATE DATABASE fast_frame;
```

2. 导入数据表
执行 `fast-serve/src/main/resources/schema.sql`

3. 配置数据库连接
编辑 `fast-serve/src/main/resources/application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/fast_frame
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

4. 启动后端
```bash
cd fast-serve
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

```bash
cd fast-web
pnpm install
pnpm dev
```

前端服务将在 http://localhost:3000 启动

### 默认账号

- 用户名：admin
- 密码：admin123

## 构建部署

### 前端构建
```bash
cd fast-web
pnpm build
```
构建产物在 `fast-web/dist/` 目录

### 后端构建
```bash
cd fast-serve
mvn clean package -DskipTests
```
构建产物在 `fast-serve/target/` 目录

## 开发指南

### API 规范

所有 API 响应遵循统一格式：
```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

### 权限控制

- 后端使用 `@SaCheckPermission` 注解控制接口权限
- 前端使用 `v-hasPermi` 指令控制按钮权限

### 数据权限

支持五种数据权限模式：
- 全部数据权限
- 自定义数据权限
- 本部门数据权限
- 本部门及以下数据权限
- 仅本人数据权限

## 许可证

MIT License