-- =============================================
-- Fast Frame 数据库初始化脚本
-- 数据库: PostgreSQL
-- =============================================

-- =============================================
-- 0. 创建数据库（需要超级用户权限执行）
-- 注意：如果已有数据库，可跳过此步骤
-- 执行方式: psql -U postgres -f schema.sql
-- 或者: 在已连接的postgres数据库中执行
-- =============================================

-- 创建数据库（如果不存在）
-- SELECT 'CREATE DATABASE fast_frame' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'fast_frame')\gexec

-- 或者直接执行（需要超级用户权限）
-- CREATE DATABASE fast_frame
--     WITH
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'en_US.UTF-8'
--     LC_CTYPE = 'en_US.UTF-8'
--     TEMPLATE = template0
--     CONNECTION LIMIT = -1;

-- 连接到数据库（命令行执行时使用）
-- \connect fast_frame

-- =============================================
-- 删除已存在的表（按依赖顺序倒序删除）
-- =============================================
DROP TABLE IF EXISTS sys_file CASCADE;
DROP TABLE IF EXISTS sys_config CASCADE;
DROP TABLE IF EXISTS sys_dict_data CASCADE;
DROP TABLE IF EXISTS sys_dict_type CASCADE;
DROP TABLE IF EXISTS sys_oper_log CASCADE;
DROP TABLE IF EXISTS sys_login_log CASCADE;
DROP TABLE IF EXISTS sys_role_menu CASCADE;
DROP TABLE IF EXISTS sys_role_dept CASCADE;
DROP TABLE IF EXISTS sys_user_role CASCADE;
DROP TABLE IF EXISTS sys_menu CASCADE;
DROP TABLE IF EXISTS sys_role CASCADE;
DROP TABLE IF EXISTS sys_dept CASCADE;
DROP TABLE IF EXISTS sys_user CASCADE;
DROP TABLE IF EXISTS sys_notice CASCADE;
DROP TABLE IF EXISTS sys_job CASCADE;
DROP TABLE IF EXISTS sys_job_log CASCADE;
DROP TABLE IF EXISTS sys_notice CASCADE;

-- =============================================
-- 删除 Warm-Flow 工作流引擎表
-- =============================================
DROP TABLE IF EXISTS flow_user CASCADE;
DROP TABLE IF EXISTS flow_his_task CASCADE;
DROP TABLE IF EXISTS flow_task CASCADE;
DROP TABLE IF EXISTS flow_instance CASCADE;
DROP TABLE IF EXISTS flow_skip CASCADE;
DROP TABLE IF EXISTS flow_node CASCADE;
DROP TABLE IF EXISTS flow_definition CASCADE;

-- =============================================
-- 1. 部门表
-- =============================================
CREATE TABLE sys_dept (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    ancestors VARCHAR(255),
    dept_name VARCHAR(50) NOT NULL,
    dept_key VARCHAR(50),
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    sort INT DEFAULT 0,
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.id IS '部门ID';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_dept.ancestors IS '祖级列表';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.dept_key IS '部门标识';
COMMENT ON COLUMN sys_dept.leader IS '负责人';
COMMENT ON COLUMN sys_dept.phone IS '联系电话';
COMMENT ON COLUMN sys_dept.email IS '邮箱';
COMMENT ON COLUMN sys_dept.sort IS '显示顺序';
COMMENT ON COLUMN sys_dept.status IS '状态(0正常 1禁用)';
COMMENT ON COLUMN sys_dept.del_flag IS '删除标志(0正常 1删除)';
COMMENT ON COLUMN sys_dept.create_by IS '创建人ID';
COMMENT ON COLUMN sys_dept.create_time IS '创建时间';
COMMENT ON COLUMN sys_dept.update_by IS '修改人ID';
COMMENT ON COLUMN sys_dept.update_time IS '修改时间';

-- =============================================
-- 2. 用户表
-- =============================================
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    dept_id BIGINT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    gender CHAR(1) DEFAULT '0',
    avatar VARCHAR(255),
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '用户ID';
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.nickname IS '昵称';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.gender IS '性别(0未知 1男 2女)';
COMMENT ON COLUMN sys_user.avatar IS '头像';
COMMENT ON COLUMN sys_user.status IS '状态(0正常 1禁用)';
COMMENT ON COLUMN sys_user.del_flag IS '删除标志(0正常 1删除)';
COMMENT ON COLUMN sys_user.create_by IS '创建人ID';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_by IS '修改人ID';
COMMENT ON COLUMN sys_user.update_time IS '修改时间';

-- =============================================
-- 3. 角色表
-- =============================================
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_key VARCHAR(50) NOT NULL,
    role_sort INT DEFAULT 0,
    data_scope CHAR(1) DEFAULT '1',
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    remark VARCHAR(255),
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '角色ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_key IS '角色权限字符串';
COMMENT ON COLUMN sys_role.role_sort IS '显示顺序';
COMMENT ON COLUMN sys_role.data_scope IS '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)';
COMMENT ON COLUMN sys_role.status IS '状态(0正常 1禁用)';
COMMENT ON COLUMN sys_role.del_flag IS '删除标志(0正常 1删除)';
COMMENT ON COLUMN sys_role.remark IS '备注';
COMMENT ON COLUMN sys_role.create_by IS '创建人ID';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_by IS '修改人ID';
COMMENT ON COLUMN sys_role.update_time IS '修改时间';

-- =============================================
-- 4. 菜单表
-- =============================================
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(50) NOT NULL,
    menu_type CHAR(1) NOT NULL,
    path VARCHAR(255),
    component VARCHAR(255),
    perms VARCHAR(100),
    icon VARCHAR(100),
    link VARCHAR(255),
    menu_sort INT DEFAULT 0,
    visible CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_menu IS '菜单权限表';
COMMENT ON COLUMN sys_menu.id IS '菜单ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型(D目录 M菜单 B按钮)';
COMMENT ON COLUMN sys_menu.path IS '路由地址';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.perms IS '权限标识';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.link IS '外链地址(iframe页面使用)';
COMMENT ON COLUMN sys_menu.menu_sort IS '显示顺序';
COMMENT ON COLUMN sys_menu.visible IS '是否显示(0显示 1隐藏)';
COMMENT ON COLUMN sys_menu.status IS '状态(0正常 1禁用)';
COMMENT ON COLUMN sys_menu.del_flag IS '删除标志(0正常 1删除)';
COMMENT ON COLUMN sys_menu.create_by IS '创建人ID';
COMMENT ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_menu.update_by IS '修改人ID';
COMMENT ON COLUMN sys_menu.update_time IS '修改时间';

-- =============================================
-- 5. 用户角色关联表
-- =============================================
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';

-- =============================================
-- 6. 角色菜单关联表
-- =============================================
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色菜单关联表';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单ID';

-- =============================================
-- 7. 角色部门关联表
-- =============================================
CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL,
    dept_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, dept_id)
);

COMMENT ON TABLE sys_role_dept IS '角色部门关联表';
COMMENT ON COLUMN sys_role_dept.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_dept.dept_id IS '部门ID';

-- =============================================
-- 8. 字典类型表
-- =============================================
CREATE TABLE sys_dict_type (
    id BIGINT PRIMARY KEY,
    dict_name VARCHAR(50) NOT NULL,
    dict_type VARCHAR(50) NOT NULL,
    status CHAR(1) DEFAULT '0',
    remark VARCHAR(255),
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE sys_dict_type IS '字典类型表';
COMMENT ON COLUMN sys_dict_type.id IS '字典主键';
COMMENT ON COLUMN sys_dict_type.dict_name IS '字典名称';
COMMENT ON COLUMN sys_dict_type.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_type.status IS '状态(0正常 1禁用)';
COMMENT ON COLUMN sys_dict_type.remark IS '备注';
COMMENT ON COLUMN sys_dict_type.create_by IS '创建人ID';
COMMENT ON COLUMN sys_dict_type.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_type.update_by IS '修改人ID';
COMMENT ON COLUMN sys_dict_type.update_time IS '修改时间';
COMMENT ON COLUMN sys_dict_type.del_flag IS '删除标志(0正常 1删除)';

-- =============================================
-- 9. 字典数据表
-- =============================================
CREATE TABLE sys_dict_data (
    id BIGINT PRIMARY KEY,
    dict_type VARCHAR(50) NOT NULL,
    dict_label VARCHAR(50) NOT NULL,
    dict_value VARCHAR(50) NOT NULL,
    dict_sort INT DEFAULT 0,
    status CHAR(1) DEFAULT '0',
    remark VARCHAR(255),
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE sys_dict_data IS '字典数据表';
COMMENT ON COLUMN sys_dict_data.id IS '字典主键';
COMMENT ON COLUMN sys_dict_data.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_data.dict_label IS '字典标签';
COMMENT ON COLUMN sys_dict_data.dict_value IS '字典键值';
COMMENT ON COLUMN sys_dict_data.dict_sort IS '字典排序';
COMMENT ON COLUMN sys_dict_data.status IS '状态(0正常 1禁用)';
COMMENT ON COLUMN sys_dict_data.remark IS '备注';
COMMENT ON COLUMN sys_dict_data.create_by IS '创建人ID';
COMMENT ON COLUMN sys_dict_data.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_data.update_by IS '修改人ID';
COMMENT ON COLUMN sys_dict_data.update_time IS '修改时间';
COMMENT ON COLUMN sys_dict_data.del_flag IS '删除标志(0正常 1删除)';

-- =============================================
-- 10. 参数配置表
-- =============================================
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY,
    config_name VARCHAR(50) NOT NULL,
    config_key VARCHAR(50) NOT NULL,
    config_value VARCHAR(255),
    config_type CHAR(1) DEFAULT '0',
    remark VARCHAR(255),
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE sys_config IS '参数配置表';
COMMENT ON COLUMN sys_config.id IS '参数主键';
COMMENT ON COLUMN sys_config.config_name IS '参数名称';
COMMENT ON COLUMN sys_config.config_key IS '参数键名';
COMMENT ON COLUMN sys_config.config_value IS '参数键值';
COMMENT ON COLUMN sys_config.config_type IS '系统内置(0是 1否)';
COMMENT ON COLUMN sys_config.remark IS '备注';
COMMENT ON COLUMN sys_config.create_by IS '创建人ID';
COMMENT ON COLUMN sys_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_config.update_by IS '修改人ID';
COMMENT ON COLUMN sys_config.update_time IS '修改时间';
COMMENT ON COLUMN sys_config.del_flag IS '删除标志(0正常 1删除)';

-- =============================================
-- 11. 登录日志表
-- =============================================
CREATE TABLE sys_login_log (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50),
    ip_address VARCHAR(50),
    login_location VARCHAR(255),
    browser VARCHAR(50),
    os VARCHAR(50),
    status CHAR(1) DEFAULT '0',
    msg VARCHAR(255),
    login_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_login_log IS '登录日志表';
COMMENT ON COLUMN sys_login_log.id IS '访问ID';
COMMENT ON COLUMN sys_login_log.username IS '用户名';
COMMENT ON COLUMN sys_login_log.ip_address IS 'IP地址';
COMMENT ON COLUMN sys_login_log.login_location IS '登录地点';
COMMENT ON COLUMN sys_login_log.browser IS '浏览器';
COMMENT ON COLUMN sys_login_log.os IS '操作系统';
COMMENT ON COLUMN sys_login_log.status IS '状态(0成功 1失败)';
COMMENT ON COLUMN sys_login_log.msg IS '提示消息';
COMMENT ON COLUMN sys_login_log.login_time IS '访问时间';

-- =============================================
-- 12. 操作日志表
-- =============================================
CREATE TABLE sys_oper_log (
    id BIGINT PRIMARY KEY,
    title VARCHAR(50),
    business_type INT,
    method VARCHAR(200),
    request_method VARCHAR(10),
    oper_name VARCHAR(50),
    oper_url VARCHAR(255),
    oper_ip VARCHAR(50),
    oper_location VARCHAR(255),
    oper_param TEXT,
    json_result TEXT,
    status CHAR(1) DEFAULT '0',
    error_msg TEXT,
    oper_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    trace_id VARCHAR(32)
);

COMMENT ON TABLE sys_oper_log IS '操作日志表';
COMMENT ON COLUMN sys_oper_log.id IS '日志主键';
COMMENT ON COLUMN sys_oper_log.title IS '模块标题';
COMMENT ON COLUMN sys_oper_log.business_type IS '操作类型（字典:sys_oper_type）';
COMMENT ON COLUMN sys_oper_log.method IS '方法名称';
COMMENT ON COLUMN sys_oper_log.request_method IS '请求方式';
COMMENT ON COLUMN sys_oper_log.oper_name IS '操作人员';
COMMENT ON COLUMN sys_oper_log.oper_url IS '请求URL';
COMMENT ON COLUMN sys_oper_log.oper_ip IS '操作IP';
COMMENT ON COLUMN sys_oper_log.oper_location IS '操作地点';
COMMENT ON COLUMN sys_oper_log.oper_param IS '请求参数';
COMMENT ON COLUMN sys_oper_log.json_result IS '返回参数';
COMMENT ON COLUMN sys_oper_log.status IS '状态(0正常 1异常)';
COMMENT ON COLUMN sys_oper_log.error_msg IS '错误消息';
COMMENT ON COLUMN sys_oper_log.oper_time IS '操作时间';
COMMENT ON COLUMN sys_oper_log.trace_id IS '追踪ID';

-- =============================================
-- 13. 文件管理表
-- =============================================
CREATE TABLE sys_file (
    id BIGINT PRIMARY KEY,
    file_name VARCHAR(100) NOT NULL,
    original_filename VARCHAR(200),
    bucket_type VARCHAR(20) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT,
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE sys_file IS '文件管理表';
COMMENT ON COLUMN sys_file.id IS '文件ID';
COMMENT ON COLUMN sys_file.file_name IS '对象名(yyyy/MM/dd/uuid.ext)';
COMMENT ON COLUMN sys_file.original_filename IS '原始文件名';
COMMENT ON COLUMN sys_file.bucket_type IS '桶类型(avatar/file)';
COMMENT ON COLUMN sys_file.content_type IS '文件类型(MIME)';
COMMENT ON COLUMN sys_file.file_size IS '文件大小(字节)';
COMMENT ON COLUMN sys_file.create_by IS '上传用户ID';
COMMENT ON COLUMN sys_file.create_time IS '上传时间';
COMMENT ON COLUMN sys_file.update_by IS '更新人ID';
COMMENT ON COLUMN sys_file.update_time IS '更新时间';
COMMENT ON COLUMN sys_file.del_flag IS '删除标志(0正常 1删除)';

-- =============================================
-- 索引
-- =============================================
CREATE INDEX idx_dept_parent ON sys_dept(parent_id);
CREATE INDEX idx_dept_ancestors ON sys_dept(ancestors);
CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_user_status ON sys_user(status);
CREATE INDEX idx_user_dept ON sys_user(dept_id);
CREATE INDEX idx_role_key ON sys_role(role_key);
CREATE INDEX idx_menu_parent ON sys_menu(parent_id);
CREATE INDEX idx_dict_type ON sys_dict_data(dict_type);
CREATE INDEX idx_config_key ON sys_config(config_key);
CREATE INDEX idx_login_time ON sys_login_log(login_time);
CREATE INDEX idx_oper_time ON sys_oper_log(oper_time);
CREATE INDEX idx_oper_trace_id ON sys_oper_log(trace_id);
CREATE INDEX idx_file_name ON sys_file(file_name);
CREATE INDEX idx_file_create_by ON sys_file(create_by);

-- =============================================
-- 初始数据
-- =============================================

-- 部门数据
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (100, 0, '总公司', 'HQ', '张总', '13800138000', 0, '0', '0', '0', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (101, 100, '深圳分公司', 'SZ', '李经理', '13800138001', 1, '0', '0', '0,100', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (102, 100, '广州分公司', 'GZ', '王经理', '13800138002', 2, '0', '0', '0,100', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (103, 101, '研发部', 'RD', '陈经理', '13800138003', 1, '0', '0', '0,100,101', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (104, 101, '市场部', 'MK', '刘经理', '13800138004', 2, '0', '0', '0,100,101', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (105, 101, '销售部', 'SL', '赵经理', '13800138005', 3, '0', '0', '0,100,101', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (106, 101, '财务部', 'FN', '孙经理', '13800138006', 4, '0', '0', '0,100,101', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (107, 102, '研发部', 'RD', '周经理', '13800138007', 1, '0', '0', '0,100,102', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (108, 102, '市场部', 'MK', '吴经理', '13800138008', 2, '0', '0', '0,100,102', 1);
INSERT INTO sys_dept (id, parent_id, dept_name, dept_key, leader, phone, sort, status, del_flag, ancestors, create_by)
VALUES (109, 102, '销售部', 'SL', '郑经理', '13800138009', 3, '0', '0', '0,100,102', 1);

-- 用户数据
-- admin 密码: admin123 (BCrypt哈希)
-- 其他用户密码: 123456 (BCrypt哈希)
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, avatar, status, del_flag, create_by)
VALUES (1, 100, 'admin', '$2a$10$FBX4UiUUE0mRcJQ3yaAvDuBw1P6cTVBCubLyyq.NDXgSmLOtnhzRK', '管理员', 'admin@fast.com', '13800000000', '1', '2026/04/12/5a3b4d4913884d2e8c7efbd204eb75e8.gif', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (2, 101, 'liling', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '李玲', 'liling@fast.com', '13800000001', '2', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (3, 102, 'wangqiang', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '王强', 'wangqiang@fast.com', '13800000002', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (4, 103, 'chenwei', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '陈伟', 'chenwei@fast.com', '13800000003', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (5, 103, 'zhangsan', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '张三', 'zhangsan@fast.com', '13800000004', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (6, 103, 'lisi', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '李四', 'lisi@fast.com', '13800000005', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (7, 104, 'wangwu', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '王五', 'wangwu@fast.com', '13800000006', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (8, 105, 'zhaoliu', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '赵六', 'zhaoliu@fast.com', '13800000007', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (9, 106, 'sunqi', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '孙七', 'sunqi@fast.com', '13800000008', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (10, 107, 'zhouba', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '周八', 'zhouba@fast.com', '13800000009', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (11, 108, 'wujiu', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '吴九', 'wujiu@fast.com', '13800000010', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (12, 109, 'zhengshi', '$2a$10$u0.MCRF1WfbRvQTgGr9sWOJ2jhryl2xNnTCos.mj.n6KY5qhmWQXG', '郑十', 'zhengshi@fast.com', '13800000011', '1', '0', '0', 1);

-- 角色数据
INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, status, del_flag, remark, create_by)
VALUES (1, '管理员', 'admin', 1, '1', '0', '0', '拥有所有权限', 1);
INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, status, del_flag, remark, create_by)
VALUES (2, '分公司经理', 'branch_manager', 2, '4', '0', '0', '可查看本部门及下级部门数据', 1);
INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, status, del_flag, remark, create_by)
VALUES (3, '部门经理', 'dept_manager', 3, '3', '0', '0', '只能查看本部门数据', 1);
INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, status, del_flag, remark, create_by)
VALUES (4, '普通员工', 'employee', 4, '5', '0', '0', '只能查看自己的数据', 1);
INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, status, del_flag, remark, create_by)
VALUES (5, '自定义权限', 'custom', 5, '2', '0', '0', '自定义数据权限(深圳研发部和市场部)', 1);

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO sys_user_role (user_id, role_id) VALUES (3, 2);
INSERT INTO sys_user_role (user_id, role_id) VALUES (4, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (5, 4);
INSERT INTO sys_user_role (user_id, role_id) VALUES (6, 4);
INSERT INTO sys_user_role (user_id, role_id) VALUES (7, 4);
INSERT INTO sys_user_role (user_id, role_id) VALUES (8, 4);
INSERT INTO sys_user_role (user_id, role_id) VALUES (9, 4);
INSERT INTO sys_user_role (user_id, role_id) VALUES (10, 3);
INSERT INTO sys_user_role (user_id, role_id) VALUES (11, 4);
INSERT INTO sys_user_role (user_id, role_id) VALUES (12, 4);
INSERT INTO sys_user_role (user_id, role_id) VALUES (5, 5);

-- =============================================
-- 菜单数据
-- =============================================

-- 系统管理目录
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, icon, menu_sort, status, del_flag, create_by)
VALUES (1, 0, '系统管理', 'D', '/system', 'Setting', 2, '0', '0', 1);

-- 用户管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (2, 1, '用户管理', 'M', '/system/user', 'system/user/index', 'User', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (3, 2, '用户详情', 'B', 'system:user:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (4, 2, '用户新增', 'B', 'system:user:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (5, 2, '用户修改', 'B', 'system:user:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (6, 2, '用户删除', 'B', 'system:user:delete', 4, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (7, 2, '重置密码', 'B', 'system:user:resetPwd', 5, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (46, 2, '用户导出', 'B', 'system:user:export', 6, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (47, 2, '用户导入', 'B', 'system:user:import', 7, '0', '0', 1);

-- 角色管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (8, 1, '角色管理', 'M', '/system/role', 'system/role/index', 'UserFilled', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (9, 8, '角色详情', 'B', 'system:role:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (10, 8, '角色新增', 'B', 'system:role:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (11, 8, '角色修改', 'B', 'system:role:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (12, 8, '角色删除', 'B', 'system:role:delete', 4, '0', '0', 1);

-- 菜单管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (13, 1, '菜单管理', 'M', '/system/menu', 'system/menu/index', 'Menu', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (14, 13, '菜单详情', 'B', 'system:menu:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (15, 13, '菜单新增', 'B', 'system:menu:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (16, 13, '菜单修改', 'B', 'system:menu:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (17, 13, '菜单删除', 'B', 'system:menu:delete', 4, '0', '0', 1);

-- 部门管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (41, 1, '部门管理', 'M', '/system/dept', 'system/dept/index', 'OfficeBuilding', 4, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (42, 41, '部门详情', 'B', 'system:dept:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (43, 41, '部门新增', 'B', 'system:dept:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (44, 41, '部门修改', 'B', 'system:dept:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (45, 41, '部门删除', 'B', 'system:dept:delete', 4, '0', '0', 1);

-- 字典管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (18, 1, '字典管理', 'M', '/system/dict', 'system/dict/index', 'Collection', 5, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (19, 18, '字典详情', 'B', 'system:dict:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (20, 18, '字典新增', 'B', 'system:dict:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (21, 18, '字典修改', 'B', 'system:dict:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (22, 18, '字典删除', 'B', 'system:dict:delete', 4, '0', '0', 1);

-- 参数配置
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (23, 1, '参数配置', 'M', '/system/config', 'system/config/index', 'Tools', 6, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (24, 23, '参数详情', 'B', 'system:config:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (25, 23, '参数新增', 'B', 'system:config:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (26, 23, '参数修改', 'B', 'system:config:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (27, 23, '参数删除', 'B', 'system:config:delete', 4, '0', '0', 1);

-- 在线用户（移到系统监控下）
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (28, 100, '在线用户', 'M', '/monitor/online', 'monitor/online/index', 'Connection', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (29, 28, '强制退出', 'B', 'monitor:online:forceLogout', 1, '0', '0', 1);

-- 日志管理目录
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, icon, menu_sort, status, del_flag, create_by)
VALUES (30, 0, '日志管理', 'D', '/log', 'Document', 3, '0', '0', 1);

-- 登录日志
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (31, 30, '登录日志', 'M', '/log/loginlog', 'log/loginlog/index', 'Document', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (32, 31, '日志查询', 'B', 'log:loginlog:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (33, 31, '日志删除', 'B', 'log:loginlog:delete', 2, '0', '0', 1);

-- 操作日志
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (34, 30, '操作日志', 'M', '/log/operlog', 'log/operlog/index', 'DocumentCopy', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (35, 34, '日志查询', 'B', 'log:operlog:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (36, 34, '日志删除', 'B', 'log:operlog:delete', 2, '0', '0', 1);

-- 文件管理（移动到系统管理下）
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (37, 1, '文件管理', 'M', '/system/file', 'system/file/index', 'Folder', 8, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (38, 37, '文件上传', 'B', 'system:file:upload', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (39, 37, '文件删除', 'B', 'system:file:delete', 2, '0', '0', 1);

-- ==================== 补充权限按钮 ====================

-- 用户分页权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (48, 2, '用户分页', 'B', 'system:user:page', 0, '0', '0', 1);

-- 角色分页权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (49, 8, '角色分页', 'B', 'system:role:page', 0, '0', '0', 1);

-- 菜单列表权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (50, 13, '菜单列表', 'B', 'system:menu:list', 0, '0', '0', 1);

-- 部门列表权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (51, 41, '部门列表', 'B', 'system:dept:list', 0, '0', '0', 1);

-- 字典分页权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (52, 18, '字典分页', 'B', 'system:dict:page', 0, '0', '0', 1);

-- 参数分页权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (53, 23, '参数分页', 'B', 'system:config:page', 0, '0', '0', 1);

-- 在线用户分页权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (54, 28, '在线分页', 'B', 'monitor:online:page', 0, '0', '0', 1);

-- 文件分页权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (55, 37, '文件分页', 'B', 'system:file:page', 0, '0', '0', 1);

-- 文件下载权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (56, 37, '文件下载', 'B', 'system:file:download', 3, '0', '0', 1);

-- 登录日志分页权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (57, 31, '日志分页', 'B', 'log:loginlog:page', 0, '0', '0', 1);

-- 操作日志分页权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (58, 34, '日志分页', 'B', 'log:operlog:page', 0, '0', '0', 1);

-- 角色列表权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (341, 8, '角色列表', 'B', 'system:role:list', 0, '0', '0', 1);

-- 字典列表权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (342, 18, '字典列表', 'B', 'system:dict:list', 0, '0', '0', 1);

-- 缓存列表权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (343, 107, '缓存列表', 'B', 'monitor:cache:list', 0, '0', '0', 1);

-- 角色菜单关联(管理员拥有所有菜单权限)
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu;

-- 分公司经理角色菜单权限(用户管理、角色管理、部门管理、流程管理-待办/已办)
-- 新增 list 权限按钮: 48(用户), 49(角色), 341(角色列表), 51(部门)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 1), (2, 2), (2, 48), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 46), (2, 47),
(2, 8), (2, 341), (2, 49), (2, 9), (2, 10), (2, 11), (2, 12),
(2, 41), (2, 51), (2, 42), (2, 43), (2, 44), (2, 45),
-- 流程管理菜单
(2, 300), (2, 310), (2, 311), (2, 314),
(2, 320), (2, 321), (2, 322), (2, 323), (2, 324), (2, 325), (2, 326),
(2, 330), (2, 331);

-- 部门经理角色菜单权限(用户管理、部门管理)
-- 新增 list 权限按钮: 48(用户), 51(部门)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(3, 1), (3, 2), (3, 48), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 46), (3, 47),
(3, 41), (3, 51), (3, 42), (3, 43), (3, 44), (3, 45);

-- 普通员工角色菜单权限(查看用户、查看部门)
-- 新增 list 权限按钮: 48(用户), 51(部门)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(4, 1), (4, 2), (4, 48), (4, 3), (4, 41), (4, 51), (4, 42);

-- 自定义权限角色菜单权限
-- 新增 list 权限按钮: 48(用户), 51(部门)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(5, 1), (5, 2), (5, 48), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 46), (5, 47),
(5, 41), (5, 51), (5, 42), (5, 43), (5, 44), (5, 45);

-- 角色部门关联(自定义数据权限角色5，可查看深圳研发部和市场部)
INSERT INTO sys_role_dept (role_id, dept_id) VALUES (5, 103), (5, 104);

-- =============================================
-- 字典数据
-- =============================================

-- 字典类型
-- 注意：修改字典数据时，需同步更新对应的枚举类或常量定义
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (1, '用户性别', 'sys_user_sex', '0', '用户性别列表', 1);
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (2, '系统状态', 'sys_normal_disable', '0', '系统状态列表', 1);
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (3, '菜单类型', 'sys_menu_type', '0', '菜单类型列表，修改时需同步 MenuType 枚举', 1);
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (4, '操作类型', 'sys_oper_type', '0', '操作类型列表，修改时需同步 BusinessType 枚举', 1);
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (5, '数据范围', 'sys_data_scope', '0', '数据范围列表', 1);

-- 字典数据
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (1, 'sys_user_sex', '男', '1', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (2, 'sys_user_sex', '女', '2', 2, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (3, 'sys_user_sex', '未知', '0', 3, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (4, 'sys_normal_disable', '正常', '0', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (5, 'sys_normal_disable', '禁用', '1', 2, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (6, 'sys_menu_type', '目录', 'D', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (7, 'sys_menu_type', '菜单', 'M', 2, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (8, 'sys_menu_type', '按钮', 'B', 3, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (9, 'sys_oper_type', '其他', '0', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (10, 'sys_oper_type', '新增', '1', 2, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (11, 'sys_oper_type', '修改', '2', 3, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (12, 'sys_oper_type', '删除', '3', 4, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (18, 'sys_oper_type', '授权', '4', 5, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (19, 'sys_oper_type', '导出', '5', 6, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (20, 'sys_oper_type', '导入', '6', 7, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (21, 'sys_oper_type', '强退', '7', 8, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (22, 'sys_oper_type', '清空', '8', 9, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (13, 'sys_data_scope', '全部数据', '1', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (14, 'sys_data_scope', '自定义数据', '2', 2, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (15, 'sys_data_scope', '本部门数据', '3', 3, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (16, 'sys_data_scope', '本部门及以下', '4', 4, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (17, 'sys_data_scope', '仅本人数据', '5', 5, '0', 1);

-- =============================================
-- 系统参数
-- =============================================
INSERT INTO sys_config (id, config_name, config_key, config_value, config_type, create_by)
VALUES (1, '系统名称', 'sys.system.name', 'Fast Frame', '0', 1);
INSERT INTO sys_config (id, config_name, config_key, config_value, config_type, create_by)
VALUES (2, '验证码开关', 'sys.captcha.enabled', 'true', '0', 1);
INSERT INTO sys_config (id, config_name, config_key, config_value, config_type, create_by)
VALUES (3, '账号初始密码', 'sys.user.initPassword', '123456', '0', 1);
INSERT INTO sys_config (id, config_name, config_key, config_value, config_type, create_by)
VALUES (4, '登录失败锁定阈值', 'sys.login.maxFailCount', '3', '0', 1);

-- =============================================
-- 14. 定时任务配置表
-- =============================================
CREATE TABLE sys_job (
    id BIGINT PRIMARY KEY,
    job_name VARCHAR(64) NOT NULL,
    job_group VARCHAR(64) NOT NULL,
    invoke_target VARCHAR(500) NOT NULL,
    cron_expression VARCHAR(255) NOT NULL,
    misfire_policy VARCHAR(20) DEFAULT '3',
    concurrent CHAR(1) DEFAULT '1',
    status CHAR(1) DEFAULT '0',
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    remark VARCHAR(500)
);

COMMENT ON TABLE sys_job IS '定时任务配置表';
COMMENT ON COLUMN sys_job.id IS '任务ID';
COMMENT ON COLUMN sys_job.job_name IS '任务名称';
COMMENT ON COLUMN sys_job.job_group IS '任务分组';
COMMENT ON COLUMN sys_job.invoke_target IS '调用目标字符串';
COMMENT ON COLUMN sys_job.cron_expression IS 'cron执行表达式';
COMMENT ON COLUMN sys_job.misfire_policy IS '计划执行错误策略(1立即执行 2执行一次 3放弃)';
COMMENT ON COLUMN sys_job.concurrent IS '是否并发执行(0允许 1禁止)';
COMMENT ON COLUMN sys_job.status IS '状态(0正常 1暂停)';
COMMENT ON COLUMN sys_job.create_by IS '创建者';
COMMENT ON COLUMN sys_job.create_time IS '创建时间';
COMMENT ON COLUMN sys_job.update_by IS '更新者';
COMMENT ON COLUMN sys_job.update_time IS '更新时间';
COMMENT ON COLUMN sys_job.del_flag IS '删除标志(0正常 1删除)';
COMMENT ON COLUMN sys_job.remark IS '备注信息';

CREATE INDEX idx_job_name ON sys_job(job_name);
CREATE INDEX idx_job_group ON sys_job(job_group);
CREATE INDEX idx_job_status ON sys_job(status);

-- =============================================
-- 15. 定时任务日志表
-- =============================================
CREATE TABLE sys_job_log (
    id BIGINT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    job_name VARCHAR(64) NOT NULL,
    job_group VARCHAR(64) NOT NULL,
    invoke_target VARCHAR(500) NOT NULL,
    job_message VARCHAR(500),
    status CHAR(1) DEFAULT '0',
    exception_info TEXT,
    start_time TIMESTAMP(0) NOT NULL,
    end_time TIMESTAMP(0)
);

COMMENT ON TABLE sys_job_log IS '定时任务日志表';
COMMENT ON COLUMN sys_job_log.id IS '日志ID';
COMMENT ON COLUMN sys_job_log.job_id IS '任务ID';
COMMENT ON COLUMN sys_job_log.job_name IS '任务名称';
COMMENT ON COLUMN sys_job_log.job_group IS '任务分组';
COMMENT ON COLUMN sys_job_log.invoke_target IS '调用目标字符串';
COMMENT ON COLUMN sys_job_log.job_message IS '日志信息';
COMMENT ON COLUMN sys_job_log.status IS '执行状态(0成功 1失败)';
COMMENT ON COLUMN sys_job_log.exception_info IS '异常信息';
COMMENT ON COLUMN sys_job_log.start_time IS '开始时间';
COMMENT ON COLUMN sys_job_log.end_time IS '结束时间';

CREATE INDEX idx_job_log_job_id ON sys_job_log(job_id);
CREATE INDEX idx_job_log_job_name ON sys_job_log(job_name);
CREATE INDEX idx_job_log_job_group ON sys_job_log(job_group);
CREATE INDEX idx_job_log_start_time ON sys_job_log(start_time);

-- =============================================
-- 系统监控菜单
-- =============================================
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, icon, menu_sort, status, del_flag, create_by)
VALUES (100, 0, '系统监控', 'D', '/monitor', 'Monitor', 4, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (101, 100, '定时任务', 'M', '/monitor/job', 'monitor/job/index', 'Timer', 2, '0', '0', 1);

-- 定时任务权限按钮
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (102, 101, '任务详情', 'B', 'monitor:job:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (103, 101, '任务新增', 'B', 'monitor:job:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (104, 101, '任务修改', 'B', 'monitor:job:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (105, 101, '任务删除', 'B', 'monitor:job:delete', 4, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (106, 101, '任务分页', 'B', 'monitor:job:page', 5, '0', '0', 1);

-- 任务日志菜单
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (110, 100, '任务日志', 'M', '/monitor/jobLog', 'monitor/jobLog/index', 'Document', 3, '0', '0', 1);

-- 任务日志权限按钮
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (111, 110, '日志查询', 'B', 'monitor:jobLog:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (112, 110, '日志删除', 'B', 'monitor:jobLog:delete', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (113, 110, '日志分页', 'B', 'monitor:jobLog:page', 3, '0', '0', 1);

-- =============================================
-- 任务相关字典数据
-- =============================================
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (10, '任务分组', 'sys_job_group', '0', '定时任务分组', 1);

INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (100, 'sys_job_group', '系统', 'SYSTEM', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (101, 'sys_job_group', '业务', 'BUSINESS', 2, '0', 1);

INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (11, '任务执行状态', 'sys_job_status', '0', '定时任务执行状态', 1);

INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (102, 'sys_job_status', '成功', '0', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (103, 'sys_job_status', '失败', '1', 2, '0', 1);

INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (12, '错过执行策略', 'sys_job_misfire', '0', '定时任务错过执行策略', 1);

INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (104, 'sys_job_misfire', '立即执行', '1', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (105, 'sys_job_misfire', '执行一次', '2', 2, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (106, 'sys_job_misfire', '放弃执行', '3', 3, '0', 1);

-- 管理员角色菜单权限（添加系统监控菜单）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 100);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 101);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 102);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 103);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 104);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 105);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 106);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 110);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 111);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 112);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 113);

-- =============================================
-- 16. 通知公告表
-- =============================================
CREATE TABLE sys_notice (
    id BIGINT PRIMARY KEY,
    notice_title VARCHAR(100) NOT NULL,
    notice_type CHAR(1) NOT NULL,
    notice_content TEXT,
    status CHAR(1) DEFAULT '0',
    create_by BIGINT,
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0'
);

COMMENT ON TABLE sys_notice IS '通知公告表';
COMMENT ON COLUMN sys_notice.id IS '公告ID';
COMMENT ON COLUMN sys_notice.notice_title IS '公告标题';
COMMENT ON COLUMN sys_notice.notice_type IS '公告类型(1通知 2公告)';
COMMENT ON COLUMN sys_notice.notice_content IS '公告内容';
COMMENT ON COLUMN sys_notice.status IS '状态(0正常 1关闭)';
COMMENT ON COLUMN sys_notice.create_by IS '创建者';
COMMENT ON COLUMN sys_notice.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice.update_by IS '更新者';
COMMENT ON COLUMN sys_notice.update_time IS '更新时间';
COMMENT ON COLUMN sys_notice.del_flag IS '删除标志(0正常 1删除)';

CREATE INDEX idx_notice_type ON sys_notice(notice_type);
CREATE INDEX idx_notice_status ON sys_notice(status);

-- =============================================
-- 缓存管理菜单
-- =============================================
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (107, 100, '缓存管理', 'M', '/monitor/cache', 'monitor/cache/index', 'Coin', 4, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (114, 107, '缓存分页', 'B', 'monitor:cache:page', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (115, 107, '缓存详情', 'B', 'monitor:cache:detail', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (116, 107, '缓存删除', 'B', 'monitor:cache:delete', 3, '0', '0', 1);

-- =============================================
-- 服务监控菜单
-- =============================================
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (108, 100, '服务监控', 'M', '/monitor/server', 'monitor/server/index', 'DataAnalysis', 5, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (117, 108, '服务器详情', 'B', 'monitor:server:detail', 1, '0', '0', 1);

-- =============================================
-- 通知公告菜单
-- =============================================
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (60, 1, '通知公告', 'M', '/system/notice', 'system/notice/index', 'Message', 9, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (61, 60, '公告详情', 'B', 'system:notice:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (62, 60, '公告新增', 'B', 'system:notice:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (63, 60, '公告修改', 'B', 'system:notice:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (64, 60, '公告删除', 'B', 'system:notice:delete', 4, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (65, 60, '公告分页', 'B', 'system:notice:page', 0, '0', '0', 1);

-- =============================================
-- 系统工具目录
-- =============================================
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, icon, menu_sort, status, del_flag, create_by)
VALUES (200, 0, '系统工具', 'D', '/tool', 'Tools', 5, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (201, 200, '接口文档', 'M', '/tool/api', 'tool/api/index', 'Document', 1, '0', '0', 1);

-- =============================================
-- 流程管理目录
-- =============================================
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, icon, menu_sort, status, del_flag, create_by)
VALUES (300, 0, '流程管理', 'D', '/flow', 'Connection', 1, '0', '0', 1);

-- 流程定义
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (301, 300, '流程定义', 'M', '/flow/def', 'flow/def/index', 'Document', 1, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (302, 301, '流程详情', 'B', 'flow:def:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (303, 301, '流程发布', 'B', 'flow:def:publish', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (304, 301, '流程取消发布', 'B', 'flow:def:unpublish', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (305, 301, '流程删除', 'B', 'flow:def:remove', 4, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (306, 301, '流程列表', 'B', 'flow:def:list', 0, '0', '0', 1);

-- 流程实例
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (310, 300, '流程实例', 'M', '/flow/instance', 'flow/instance/index', 'DocumentCopy', 2, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (311, 310, '实例详情', 'B', 'flow:instance:detail', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (312, 310, '发起流程', 'B', 'flow:instance:start', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (313, 310, '终止流程', 'B', 'flow:instance:terminate', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (314, 310, '实例列表', 'B', 'flow:instance:list', 0, '0', '0', 1);

-- 待办任务
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (320, 300, '待办任务', 'M', '/flow/task/todo', 'flow/task/todo', 'Bell', 3, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (321, 320, '任务审批', 'B', 'flow:task:approve', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (322, 320, '任务驳回', 'B', 'flow:task:reject', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (323, 320, '任务退回', 'B', 'flow:task:back', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (324, 320, '任务委派', 'B', 'flow:task:delegate', 4, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (325, 320, '待办列表', 'B', 'flow:task:todo', 0, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (326, 320, '任务详情', 'B', 'flow:task:detail', 5, '0', '0', 1);

-- 已办任务
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (330, 300, '已办任务', 'M', '/flow/task/done', 'flow/task/done', 'Finished', 4, '0', '0', 1);

INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (331, 330, '已办列表', 'B', 'flow:task:done', 0, '0', '0', 1);

-- 流程设计器
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (340, 300, '流程设计', 'M', '/flow/designer', 'flow/designer/index', 'Edit', 5, '0', '0', 1);

-- =============================================
-- 新增菜单权限
-- =============================================
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 107);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 114);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 115);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 116);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 108);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 117);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 109);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 60);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 61);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 62);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 63);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 64);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 65);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 200);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 201);

-- 流程管理菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 300);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 301);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 302);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 303);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 304);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 305);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 306);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 310);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 311);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 312);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 313);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 314);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 320);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 321);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 322);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 323);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 324);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 325);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 326);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 330);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 331);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 340);

-- =============================================
-- 通知类型字典
-- =============================================
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_by)
VALUES (20, '通知类型', 'sys_notice_type', '0', '通知类型列表', 1);

INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (200, 'sys_notice_type', '通知', '1', 1, '0', 1);
INSERT INTO sys_dict_data (id, dict_type, dict_label, dict_value, dict_sort, status, create_by)
VALUES (201, 'sys_notice_type', '公告', '2', 2, '0', 1);

-- =============================================
-- Warm-Flow 工作流引擎表结构 (PostgreSQL)
-- =============================================

-- 1. 流程定义表
CREATE TABLE flow_definition (
    id BIGINT PRIMARY KEY,
    flow_code VARCHAR(40) NOT NULL,
    flow_name VARCHAR(100) NOT NULL,
    model_value VARCHAR(40) DEFAULT 'CLASSICS',
    category VARCHAR(100),
    version VARCHAR(20) NOT NULL,
    is_publish SMALLINT DEFAULT 0,
    form_custom CHAR(1) DEFAULT 'N',
    form_path VARCHAR(100),
    activity_status SMALLINT DEFAULT 1,
    listener_type VARCHAR(100),
    listener_path VARCHAR(400),
    ext VARCHAR(500),
    create_by VARCHAR(80),
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(80),
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    tenant_id VARCHAR(40)
);

COMMENT ON TABLE flow_definition IS '流程定义表';
COMMENT ON COLUMN flow_definition.id IS '主键id';
COMMENT ON COLUMN flow_definition.flow_code IS '流程编码';
COMMENT ON COLUMN flow_definition.flow_name IS '流程名称';
COMMENT ON COLUMN flow_definition.model_value IS '设计器模型(CLASSICS经典 MIMIC仿钉钉)';
COMMENT ON COLUMN flow_definition.category IS '流程类别';
COMMENT ON COLUMN flow_definition.version IS '流程版本';
COMMENT ON COLUMN flow_definition.is_publish IS '是否发布(0未发布 1已发布 9失效)';
COMMENT ON COLUMN flow_definition.form_custom IS '审批表单是否自定义(Y是 N否)';
COMMENT ON COLUMN flow_definition.form_path IS '审批表单路径';
COMMENT ON COLUMN flow_definition.activity_status IS '流程激活状态(0挂起 1激活)';
COMMENT ON COLUMN flow_definition.listener_type IS '监听器类型';
COMMENT ON COLUMN flow_definition.listener_path IS '监听器路径';
COMMENT ON COLUMN flow_definition.ext IS '业务详情(存业务表对象json字符串)';
COMMENT ON COLUMN flow_definition.create_by IS '创建者';
COMMENT ON COLUMN flow_definition.create_time IS '创建时间';
COMMENT ON COLUMN flow_definition.update_by IS '更新者';
COMMENT ON COLUMN flow_definition.update_time IS '更新时间';
COMMENT ON COLUMN flow_definition.del_flag IS '删除标志(0正常 2删除)';
COMMENT ON COLUMN flow_definition.tenant_id IS '租户id';

CREATE UNIQUE INDEX idx_flow_def_code_version ON flow_definition(flow_code, version) WHERE del_flag = '0';

-- 2. 流程节点表
CREATE TABLE flow_node (
    id BIGINT PRIMARY KEY,
    node_type SMALLINT NOT NULL,
    definition_id BIGINT NOT NULL,
    node_code VARCHAR(100) NOT NULL,
    node_name VARCHAR(100),
    permission_flag VARCHAR(200),
    node_ratio VARCHAR(200),
    coordinate VARCHAR(100),
    any_node_skip VARCHAR(100),
    listener_type VARCHAR(100),
    listener_path VARCHAR(400),
    handler_type VARCHAR(100),
    handler_path VARCHAR(400),
    form_custom CHAR(1) DEFAULT 'N',
    form_path VARCHAR(100),
    version VARCHAR(20) NOT NULL,
    create_by VARCHAR(80),
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(80),
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    ext TEXT,
    del_flag CHAR(1) DEFAULT '0',
    tenant_id VARCHAR(40)
);

COMMENT ON TABLE flow_node IS '流程节点表';
COMMENT ON COLUMN flow_node.id IS '主键id';
COMMENT ON COLUMN flow_node.node_type IS '节点类型(0开始 1中间 2结束 3互斥网关 4并行网关)';
COMMENT ON COLUMN flow_node.definition_id IS '流程定义id';
COMMENT ON COLUMN flow_node.node_code IS '流程节点编码';
COMMENT ON COLUMN flow_node.node_name IS '流程节点名称';
COMMENT ON COLUMN flow_node.permission_flag IS '权限标识(权限类型:权限标识,多个用@@隔开)';
COMMENT ON COLUMN flow_node.node_ratio IS '流程签署比例值';
COMMENT ON COLUMN flow_node.coordinate IS '坐标';
COMMENT ON COLUMN flow_node.any_node_skip IS '任意结点跳转';
COMMENT ON COLUMN flow_node.listener_type IS '监听器类型';
COMMENT ON COLUMN flow_node.listener_path IS '监听器路径';
COMMENT ON COLUMN flow_node.handler_type IS '处理器类型';
COMMENT ON COLUMN flow_node.handler_path IS '处理器路径';
COMMENT ON COLUMN flow_node.form_custom IS '审批表单是否自定义(Y是 N否)';
COMMENT ON COLUMN flow_node.form_path IS '审批表单路径';
COMMENT ON COLUMN flow_node.version IS '版本';
COMMENT ON COLUMN flow_node.create_by IS '创建者';
COMMENT ON COLUMN flow_node.create_time IS '创建时间';
COMMENT ON COLUMN flow_node.update_by IS '更新者';
COMMENT ON COLUMN flow_node.update_time IS '更新时间';
COMMENT ON COLUMN flow_node.ext IS '节点扩展属性';
COMMENT ON COLUMN flow_node.del_flag IS '删除标志(0正常 2删除)';
COMMENT ON COLUMN flow_node.tenant_id IS '租户id';

CREATE INDEX idx_flow_node_def ON flow_node(definition_id);
CREATE INDEX idx_flow_node_code ON flow_node(node_code);

-- 3. 流程跳转关联表
CREATE TABLE flow_skip (
    id BIGINT PRIMARY KEY,
    definition_id BIGINT NOT NULL,
    now_node_code VARCHAR(100) NOT NULL,
    now_node_type SMALLINT,
    next_node_code VARCHAR(100) NOT NULL,
    next_node_type SMALLINT,
    skip_name VARCHAR(100),
    skip_type VARCHAR(40),
    skip_condition VARCHAR(200),
    coordinate VARCHAR(100),
    create_by VARCHAR(80),
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(80),
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    tenant_id VARCHAR(40)
);

COMMENT ON TABLE flow_skip IS '流程跳转关联表';
COMMENT ON COLUMN flow_skip.id IS '主键id';
COMMENT ON COLUMN flow_skip.definition_id IS '流程定义id';
COMMENT ON COLUMN flow_skip.now_node_code IS '当前流程节点编码';
COMMENT ON COLUMN flow_skip.now_node_type IS '当前节点类型(0开始 1中间 2结束 3互斥网关 4并行网关)';
COMMENT ON COLUMN flow_skip.next_node_code IS '下一个流程节点编码';
COMMENT ON COLUMN flow_skip.next_node_type IS '下一节点类型(0开始 1中间 2结束 3互斥网关 4并行网关)';
COMMENT ON COLUMN flow_skip.skip_name IS '跳转名称';
COMMENT ON COLUMN flow_skip.skip_type IS '跳转类型(PASS审批通过 REJECT退回)';
COMMENT ON COLUMN flow_skip.skip_condition IS '跳转条件';
COMMENT ON COLUMN flow_skip.coordinate IS '坐标';
COMMENT ON COLUMN flow_skip.create_by IS '创建者';
COMMENT ON COLUMN flow_skip.create_time IS '创建时间';
COMMENT ON COLUMN flow_skip.update_by IS '更新者';
COMMENT ON COLUMN flow_skip.update_time IS '更新时间';
COMMENT ON COLUMN flow_skip.del_flag IS '删除标志(0正常 2删除)';
COMMENT ON COLUMN flow_skip.tenant_id IS '租户id';

CREATE INDEX idx_flow_skip_def ON flow_skip(definition_id);
CREATE INDEX idx_flow_skip_now ON flow_skip(now_node_code);
CREATE INDEX idx_flow_skip_next ON flow_skip(next_node_code);

-- 4. 流程实例表
CREATE TABLE flow_instance (
    id BIGINT PRIMARY KEY,
    definition_id BIGINT NOT NULL,
    business_id VARCHAR(40) NOT NULL,
    node_type SMALLINT NOT NULL,
    node_code VARCHAR(40) NOT NULL,
    node_name VARCHAR(100),
    variable TEXT,
    flow_status VARCHAR(20) NOT NULL,
    activity_status SMALLINT DEFAULT 1,
    def_json TEXT,
    create_by VARCHAR(64),
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(80),
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    ext VARCHAR(500),
    del_flag CHAR(1) DEFAULT '0',
    tenant_id VARCHAR(40)
);

COMMENT ON TABLE flow_instance IS '流程实例表';
COMMENT ON COLUMN flow_instance.id IS '主键id';
COMMENT ON COLUMN flow_instance.definition_id IS '流程定义id';
COMMENT ON COLUMN flow_instance.business_id IS '业务id';
COMMENT ON COLUMN flow_instance.node_type IS '节点类型(0开始 1中间 2结束 3互斥网关 4并行网关)';
COMMENT ON COLUMN flow_instance.node_code IS '流程节点编码';
COMMENT ON COLUMN flow_instance.node_name IS '流程节点名称';
COMMENT ON COLUMN flow_instance.variable IS '任务变量';
COMMENT ON COLUMN flow_instance.flow_status IS '流程状态(0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回)';
COMMENT ON COLUMN flow_instance.activity_status IS '流程激活状态(0挂起 1激活)';
COMMENT ON COLUMN flow_instance.def_json IS '流程定义json';
COMMENT ON COLUMN flow_instance.create_by IS '创建者';
COMMENT ON COLUMN flow_instance.create_time IS '创建时间';
COMMENT ON COLUMN flow_instance.update_by IS '更新者';
COMMENT ON COLUMN flow_instance.update_time IS '更新时间';
COMMENT ON COLUMN flow_instance.ext IS '扩展字段';
COMMENT ON COLUMN flow_instance.del_flag IS '删除标志(0正常 2删除)';
COMMENT ON COLUMN flow_instance.tenant_id IS '租户id';

CREATE INDEX idx_flow_ins_def ON flow_instance(definition_id);
CREATE INDEX idx_flow_ins_business ON flow_instance(business_id);
CREATE INDEX idx_flow_ins_status ON flow_instance(flow_status);

-- 5. 待办任务表
CREATE TABLE flow_task (
    id BIGINT PRIMARY KEY,
    definition_id BIGINT NOT NULL,
    instance_id BIGINT NOT NULL,
    node_code VARCHAR(100) NOT NULL,
    node_name VARCHAR(100),
    node_type SMALLINT NOT NULL,
    flow_status VARCHAR(20) NOT NULL,
    form_custom CHAR(1) DEFAULT 'N',
    form_path VARCHAR(100),
    create_by VARCHAR(80),
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(80),
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    tenant_id VARCHAR(40)
);

COMMENT ON TABLE flow_task IS '待办任务表';
COMMENT ON COLUMN flow_task.id IS '主键id';
COMMENT ON COLUMN flow_task.definition_id IS '流程定义id';
COMMENT ON COLUMN flow_task.instance_id IS '流程实例id';
COMMENT ON COLUMN flow_task.node_code IS '节点编码';
COMMENT ON COLUMN flow_task.node_name IS '节点名称';
COMMENT ON COLUMN flow_task.node_type IS '节点类型(0开始 1中间 2结束 3互斥网关 4并行网关)';
COMMENT ON COLUMN flow_task.flow_status IS '流程状态(0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回)';
COMMENT ON COLUMN flow_task.form_custom IS '审批表单是否自定义(Y是 N否)';
COMMENT ON COLUMN flow_task.form_path IS '审批表单路径';
COMMENT ON COLUMN flow_task.create_by IS '创建者';
COMMENT ON COLUMN flow_task.create_time IS '创建时间';
COMMENT ON COLUMN flow_task.update_by IS '更新者';
COMMENT ON COLUMN flow_task.update_time IS '更新时间';
COMMENT ON COLUMN flow_task.del_flag IS '删除标志(0正常 2删除)';
COMMENT ON COLUMN flow_task.tenant_id IS '租户id';

CREATE INDEX idx_flow_task_ins ON flow_task(instance_id);
CREATE INDEX idx_flow_task_node ON flow_task(node_code);
CREATE INDEX idx_flow_task_status ON flow_task(flow_status);

-- 6. 历史任务记录表
CREATE TABLE flow_his_task (
    id BIGINT PRIMARY KEY,
    definition_id BIGINT NOT NULL,
    instance_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    node_code VARCHAR(100),
    node_name VARCHAR(100),
    node_type SMALLINT,
    target_node_code VARCHAR(200),
    target_node_name VARCHAR(200),
    approver VARCHAR(40),
    cooperate_type SMALLINT DEFAULT 0,
    collaborator VARCHAR(500),
    skip_type VARCHAR(10) NOT NULL,
    flow_status VARCHAR(20) NOT NULL,
    form_custom CHAR(1) DEFAULT 'N',
    form_path VARCHAR(100),
    message VARCHAR(500),
    variable TEXT,
    ext TEXT,
    create_by VARCHAR(80),
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(80),
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    tenant_id VARCHAR(40)
);

COMMENT ON TABLE flow_his_task IS '历史任务记录表';
COMMENT ON COLUMN flow_his_task.id IS '主键id';
COMMENT ON COLUMN flow_his_task.definition_id IS '流程定义id';
COMMENT ON COLUMN flow_his_task.instance_id IS '流程实例id';
COMMENT ON COLUMN flow_his_task.task_id IS '待办任务id';
COMMENT ON COLUMN flow_his_task.node_code IS '开始节点编码';
COMMENT ON COLUMN flow_his_task.node_name IS '开始节点名称';
COMMENT ON COLUMN flow_his_task.node_type IS '开始节点类型(0开始 1中间 2结束 3互斥网关 4并行网关)';
COMMENT ON COLUMN flow_his_task.target_node_code IS '目标节点编码';
COMMENT ON COLUMN flow_his_task.target_node_name IS '结束节点名称';
COMMENT ON COLUMN flow_his_task.approver IS '审批者';
COMMENT ON COLUMN flow_his_task.cooperate_type IS '协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)';
COMMENT ON COLUMN flow_his_task.collaborator IS '协作人';
COMMENT ON COLUMN flow_his_task.skip_type IS '流转类型(PASS通过 REJECT退回 NONE无动作)';
COMMENT ON COLUMN flow_his_task.flow_status IS '流程状态(0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回)';
COMMENT ON COLUMN flow_his_task.form_custom IS '审批表单是否自定义(Y是 N否)';
COMMENT ON COLUMN flow_his_task.form_path IS '审批表单路径';
COMMENT ON COLUMN flow_his_task.message IS '审批意见';
COMMENT ON COLUMN flow_his_task.variable IS '任务变量';
COMMENT ON COLUMN flow_his_task.ext IS '业务详情(存业务表对象json字符串)';
COMMENT ON COLUMN flow_his_task.create_by IS '创建者';
COMMENT ON COLUMN flow_his_task.create_time IS '任务开始时间';
COMMENT ON COLUMN flow_his_task.update_by IS '更新者';
COMMENT ON COLUMN flow_his_task.update_time IS '审批完成时间';
COMMENT ON COLUMN flow_his_task.del_flag IS '删除标志(0正常 2删除)';
COMMENT ON COLUMN flow_his_task.tenant_id IS '租户id';

CREATE INDEX idx_flow_his_ins ON flow_his_task(instance_id);
CREATE INDEX idx_flow_his_task ON flow_his_task(task_id);

-- 7. 流程用户表
CREATE TABLE flow_user (
    id BIGINT PRIMARY KEY,
    type CHAR(1) NOT NULL,
    processed_by VARCHAR(80),
    associated BIGINT NOT NULL,
    create_by VARCHAR(80),
    create_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(80),
    update_time TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    tenant_id VARCHAR(40)
);

COMMENT ON TABLE flow_user IS '流程用户表';
COMMENT ON COLUMN flow_user.id IS '主键id';
COMMENT ON COLUMN flow_user.type IS '人员类型(1待办任务审批人权限 2待办任务转办人权限 3待办任务委托人权限)';
COMMENT ON COLUMN flow_user.processed_by IS '权限人';
COMMENT ON COLUMN flow_user.associated IS '任务表id';
COMMENT ON COLUMN flow_user.create_by IS '创建人';
COMMENT ON COLUMN flow_user.create_time IS '创建时间';
COMMENT ON COLUMN flow_user.update_by IS '更新人';
COMMENT ON COLUMN flow_user.update_time IS '更新时间';
COMMENT ON COLUMN flow_user.del_flag IS '删除标志(0正常 2删除)';
COMMENT ON COLUMN flow_user.tenant_id IS '租户id';

CREATE INDEX idx_flow_user_associated ON flow_user(associated);
CREATE INDEX idx_flow_user_processed ON flow_user(processed_by);

-- =============================================
-- 初始化请假审批流程
-- 布局说明：
-- - 主轴线 y=200 (前进线)
-- - 上方绕行 y=50 (直接通过)
-- - 驳回线1 y=320 (分公司经理驳回)
-- - 驳回线2 y=380 (部门经理驳回)
-- =============================================

-- 流程定义
INSERT INTO flow_definition (id, flow_code, flow_name, model_value, category, version, is_publish, activity_status, form_custom, del_flag)
VALUES (2000000000000000001, 'leave_flow', '请假审批流程', 'CLASSICS', '人事管理', '1', 1, 1, 'N', '0');

-- 流程节点 (节点中心坐标|文字坐标)
-- 开始节点(圆形40px)：x=120, 文字在下方
INSERT INTO flow_node (id, node_type, definition_id, node_code, node_name, coordinate, node_ratio, form_custom, version, del_flag)
VALUES (2000000000000000002, 0, 2000000000000000001, 'a', '开始', '120,200|120,260', '0', 'N', '1', '0');

-- 分公司经理审批(矩形100x80px)：x=320
INSERT INTO flow_node (id, node_type, definition_id, node_code, node_name, permission_flag, coordinate, node_ratio, form_custom, version, del_flag)
VALUES (2000000000000000003, 1, 2000000000000000001, 'b', '分公司经理审批', 'role:branch_manager', '320,200|320,200', '0', 'N', '1', '0');

-- 部门经理审批(矩形100x80px)：x=520
INSERT INTO flow_node (id, node_type, definition_id, node_code, node_name, permission_flag, coordinate, node_ratio, form_custom, version, del_flag)
VALUES (2000000000000000004, 1, 2000000000000000001, 'c', '部门经理审批', 'role:dept_manager', '520,200|520,200', '0', 'N', '1', '0');

-- 结束节点(圆形40px)：x=720
INSERT INTO flow_node (id, node_type, definition_id, node_code, node_name, coordinate, node_ratio, form_custom, version, del_flag)
VALUES (2000000000000000005, 2, 2000000000000000001, 'd', '结束', '720,200|720,260', '0', 'N', '1', '0');

-- 流程跳转线 (路径点|文字坐标)
-- 1. 开始 -> 分公司经理审批 (提交申请) - 主轴线
INSERT INTO flow_skip (id, definition_id, now_node_code, now_node_type, next_node_code, next_node_type, skip_name, skip_type, coordinate, del_flag)
VALUES (2000000000000000006, 2000000000000000001, 'a', 0, 'b', 1, '提交申请', 'PASS', '140,200;270,200|205,200', '0');

-- 2. 分公司经理审批 -> 部门经理审批 (同意-需部门审批) - 主轴线
INSERT INTO flow_skip (id, definition_id, now_node_code, now_node_type, next_node_code, next_node_type, skip_name, skip_type, skip_condition, coordinate, del_flag)
VALUES (2000000000000000007, 2000000000000000001, 'b', 1, 'c', 1, '同意(需部门审批)', 'PASS', 'gt@@day|3', '370,200;470,200|420,200', '0');

-- 3. 分公司经理审批 -> 结束 (同意-直接通过) - 上方绕行 y=50
INSERT INTO flow_skip (id, definition_id, now_node_code, now_node_type, next_node_code, next_node_type, skip_name, skip_type, skip_condition, coordinate, del_flag)
VALUES (2000000000000000008, 2000000000000000001, 'b', 1, 'd', 2, '同意(直接通过)', 'PASS', 'le@@day|3', '370,200;370,50;700,50;700,180|535,50', '0');

-- 4. 分公司经理审批 -> 开始 (驳回) - 下方绕行 y=320
INSERT INTO flow_skip (id, definition_id, now_node_code, now_node_type, next_node_code, next_node_type, skip_name, skip_type, coordinate, del_flag)
VALUES (2000000000000000009, 2000000000000000001, 'b', 1, 'a', 0, '驳回', 'REJECT', '270,200;270,320;120,320;120,220|195,320', '0');

-- 5. 部门经理审批 -> 结束 (同意) - 主轴线
INSERT INTO flow_skip (id, definition_id, now_node_code, now_node_type, next_node_code, next_node_type, skip_name, skip_type, coordinate, del_flag)
VALUES (2000000000000000010, 2000000000000000001, 'c', 1, 'd', 2, '同意', 'PASS', '570,200;700,200|635,200', '0');

-- 6. 部门经理审批 -> 分公司经理审批 (驳回) - 最下方绕行 y=380
INSERT INTO flow_skip (id, definition_id, now_node_code, now_node_type, next_node_code, next_node_type, skip_name, skip_type, coordinate, del_flag)
VALUES (2000000000000000011, 2000000000000000001, 'c', 1, 'b', 1, '驳回', 'REJECT', '470,200;470,380;270,380;270,200|370,380', '0');

