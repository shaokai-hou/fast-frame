# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Fast Frame 是一个全栈后台管理系统，前端使用 Vue 3，后端使用 Spring Boot。

**前端** (`fast-web/`): Vue 3 + Vite 5 + Element Plus + Pinia + Vue Router
**后端** (`fast-serve/`): Spring Boot 2.7 + MyBatis Plus + Sa-Token + PostgreSQL + Redis + MinIO

## 构建命令

### 前端 (fast-web)
```bash
cd fast-web
pnpm install          # 安装依赖
pnpm dev              # 启动开发服务器 (端口 3000，/api 代理到 :8080)
pnpm build            # 生产构建到 dist/
pnpm preview          # 预览生产构建
```

### 后端 (fast-serve)
```bash
cd fast-serve
mvn clean install     # 构建
mvn spring-boot:run   # 运行 (端口 8080)
mvn test              # 运行单元测试
```

## 架构

### 前端结构
```
src/
├── api/           # API 模块 (auth.js, user.js 等)，使用 axios
├── components/    # 共享组件 (Pagination, TreeSelect, IconSelect, SearchBar)
├── directives/    # 自定义 Vue 指令 (v-hasPermi 权限指令)
├── layout/        # 主布局，包含 Sidebar, Navbar (Breadcrumb, UserDropdown, Screenfull)
├── router/        # 动态路由生成 + 静态路由
├── store/         # Pinia 状态管理
├── styles/        # SCSS 样式 (element.scss 用于 Element Plus 自定义, dark.scss 深色主题)
├── utils/         # request.js (axios 拦截器), auth.js (token 管理)
├── views/         # 页面组件 (login, home, system/*, log/*, profile)
└── permission.js  # 路由守卫，白名单机制
```

### 后端结构
```
src/main/java/com/fast/
├── common/        # Result, PageResult, 常量, 异常, 工具类
├── framework/     # 配置类 (SaToken, Redis, MybatisPlus, MinIO), AOP, 注解
├── modules/
│   ├── auth/      # 登录、验证码、用户信息、路由接口
│   ├── file/      # 文件上传下载 (MinIO)
│   ├── log/       # 操作日志、登录日志
│   ├── online/    # 在线用户管理
│   └── system/    # 用户、角色、菜单、部门、字典、配置 CRUD
│       └── domain/
│           ├── entity/    # 数据库实体类（与表结构对应）
│           ├── dto/       # 数据传输对象（接收请求参数）
│           └── vo/        # 视图对象（返回给前端）
```

### 关键模式

**动态路由**: 前端从 `/auth/routes` 获取路由数据，通过 `generateRoutes()` 转换后动态添加到 Vue Router。组件路径通过 `import.meta.glob('/src/views/**/*.vue')` 预加载。

**认证机制**: Sa-Token + JWT。Token 存储在 Redis，通过 `sa-token` header 传递（格式：`Bearer ${token}`）。

**API 响应格式**:
```json
{ "code": 200, "msg": "success", "data": {...} }
```
code 为 401 时跳转到登录页。

**Element Plus 全局配置**: 在 `main.js` 中设置 Dialog 的 `closeOnClickModal` 为 `false`。

**权限控制**: 
- 后端：控制器使用 `@RequiresPermission` 注解，支持 AND/OR 逻辑
- 前端：路由守卫检查 token，指令 `v-hasPermi` 控制按钮权限

**数据权限**: 后端使用 `@DataScope` 注解 + AOP 实现，支持全部数据、自定义、本部门、本部门及以下、仅本人五种模式。

**日志记录**: 使用 `@Log` 注解进行 AOP 操作日志记录。

## 共享组件

### 全局组件
以下组件在 `main.js` 中全局注册，无需导入即可直接使用：
- **Pagination** - 分页组件
- **TreeSelect** - 树形选择组件
- **Element Plus Icons** - 所有 `@element-plus/icons-vue` 图标（如 `<Edit />`、`<Delete />`）

### TreeSelect
支持单选和多选模式：
```vue
<!-- 单选 -->
<TreeSelect v-model="form.deptId" :data="treeData" />

<!-- 多选（带复选框） -->
<TreeSelect
  v-model="form.menuIds"
  :data="menuOptions"
  :field-props="{ label: 'label', children: 'children', value: 'id' }"
  value-key="id"
  multiple
  show-checkbox
  check-on-click-node
  default-expand-all
/>
```

### Pagination
分页组件，配合 `v-model:page` 和 `v-model:limit` 使用：
```vue
<Pagination :total="total" v-model:page="pageNum" v-model:limit="pageSize" @pagination="getList" />
```

## 环境配置

**前端** (`.env.development`): `VITE_API_URL=/api` (代理)
**后端** (`application-dev.yml`): PostgreSQL 端口 5432，Redis 端口 6379，MinIO 端口 9000

## 开发说明

- Element Plus 使用中文 locale (zh-cn)
- 支持深色主题 (`element-plus/theme-chalk/dark/css-vars.css`)
- SCSS 使用 modern-compiler API (`vite.config.js` 中配置)
- MyBatis Plus 逻辑删除字段为 `delFlag`
- Vite 别名 `@` 映射到 `src/` 目录
- 树形数据结构：`{ id, label, children }` 或通过 `fieldProps` 映射
- **Redis 缓存 Key**: 所有 Redis 缓存 key 常量统一在 `RedisKeyConstants.java` 管理，不与通用常量混用

## 文件处理能力

### 前端文件预览
浏览器内直接预览 Office 文档，使用 `@vue-office` 系列：
- `@vue-office/pdf` - PDF 文件预览
- `@vue-office/docx` - Word 文档预览
- `@vue-office/excel` - Excel 表格预览
- `@vue-office/pptx` - PowerPoint 演示文稿预览

### 文件下载
使用 `file-saver` 包处理文件下载，通过 `request.js` 导出的 `download()` 函数：
```js
import { download } from '@/utils/request'
download('/file/download?id=1', 'filename.xlsx')
```

### Excel 导入导出
- **后端**: EasyExcel (3.3.4) - 高性能 Excel 处理，配合 `ExcelUtil` 工具类

## Java 注释规范

- **类级别 Javadoc**: 必须有 `@author` 标注
- **方法级别 Javadoc**: 必须有 `@param`（每个参数）和 `@return`（有返回值时）
- **方法内代码注释**: 单行注释 `//`，独立写在代码上方（禁止使用多行注释 `/* */`）
- **禁止分隔注释**: 方法间不使用 `// ==================== xxx ====================` 分隔注释，通过 Javadoc 自然分组。

## 后端快速验证

```bash
mvn clean compile -DskipTests -q  # 快速编译验证，不运行测试
```

## SQL 变更管理

所有数据库变更合并到 `schema.sql`，不创建单独的增量文件。已有数据库需手动执行新增 SQL。