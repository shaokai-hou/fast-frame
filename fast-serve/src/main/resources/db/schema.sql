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
-- admin 密码: admin123 (MD5: 0192023a7bbd73250516f069df18b500)
-- 其他用户密码: 123456 (MD5: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (1, 100, 'admin', '0192023a7bbd73250516f069df18b500', '管理员', 'admin@fast.com', '13800000000', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (2, 101, 'liling', 'e10adc3949ba59abbe56e057f20f883e', '李玲', 'liling@fast.com', '13800000001', '2', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (3, 102, 'wangqiang', 'e10adc3949ba59abbe56e057f20f883e', '王强', 'wangqiang@fast.com', '13800000002', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (4, 103, 'chenwei', 'e10adc3949ba59abbe56e057f20f883e', '陈伟', 'chenwei@fast.com', '13800000003', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (5, 103, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@fast.com', '13800000004', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (6, 103, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@fast.com', '13800000005', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (7, 104, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'wangwu@fast.com', '13800000006', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (8, 105, 'zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', 'zhaoliu@fast.com', '13800000007', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (9, 106, 'sunqi', 'e10adc3949ba59abbe56e057f20f883e', '孙七', 'sunqi@fast.com', '13800000008', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (10, 107, 'zhouba', 'e10adc3949ba59abbe56e057f20f883e', '周八', 'zhouba@fast.com', '13800000009', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (11, 108, 'wujiu', 'e10adc3949ba59abbe56e057f20f883e', '吴九', 'wujiu@fast.com', '13800000010', '1', '0', '0', 1);
INSERT INTO sys_user (id, dept_id, username, password, nickname, email, phone, gender, status, del_flag, create_by)
VALUES (12, 109, 'zhengshi', 'e10adc3949ba59abbe56e057f20f883e', '郑十', 'zhengshi@fast.com', '13800000011', '1', '0', '0', 1);

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
VALUES (1, 0, '系统管理', 'D', '/system', 'Setting', 1, '0', '0', 1);

-- 用户管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (2, 1, '用户管理', 'M', '/system/user', 'system/user/index', 'User', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (3, 2, '用户查询', 'B', 'system:user:query', 1, '0', '0', 1);
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
VALUES (9, 8, '角色查询', 'B', 'system:role:query', 1, '0', '0', 1);
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
VALUES (14, 13, '菜单查询', 'B', 'system:menu:query', 1, '0', '0', 1);
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
VALUES (42, 41, '部门查询', 'B', 'system:dept:query', 1, '0', '0', 1);
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
VALUES (19, 18, '字典查询', 'B', 'system:dict:query', 1, '0', '0', 1);
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
VALUES (24, 23, '参数查询', 'B', 'system:config:query', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (25, 23, '参数新增', 'B', 'system:config:add', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (26, 23, '参数修改', 'B', 'system:config:edit', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (27, 23, '参数删除', 'B', 'system:config:delete', 4, '0', '0', 1);

-- 在线用户
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (28, 1, '在线用户', 'M', '/system/online', 'system/online/index', 'Connection', 7, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (29, 28, '强制退出', 'B', 'system:online:forceLogout', 1, '0', '0', 1);

-- 日志管理目录
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, icon, menu_sort, status, del_flag, create_by)
VALUES (30, 0, '日志管理', 'D', '/log', 'Document', 2, '0', '0', 1);

-- 登录日志
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (31, 30, '登录日志', 'M', '/log/loginlog', 'log/loginlog/index', 'Document', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (32, 31, '日志查询', 'B', 'system:loginlog:query', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (33, 31, '日志删除', 'B', 'system:loginlog:delete', 2, '0', '0', 1);

-- 操作日志
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (34, 30, '操作日志', 'M', '/log/operlog', 'log/operlog/index', 'DocumentCopy', 2, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (35, 34, '日志查询', 'B', 'system:operlog:query', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (36, 34, '日志删除', 'B', 'system:operlog:delete', 2, '0', '0', 1);

-- 文件管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, menu_sort, status, del_flag, create_by)
VALUES (37, 0, '文件管理', 'M', '/file', 'file/index', 'Folder', 3, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (38, 37, '文件上传', 'B', 'system:file:upload', 1, '0', '0', 1);
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, perms, menu_sort, status, del_flag, create_by)
VALUES (39, 37, '文件删除', 'B', 'system:file:delete', 2, '0', '0', 1);

-- 角色菜单关联(管理员拥有所有菜单权限)
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu;

-- 分公司经理角色菜单权限(用户管理、角色管理、部门管理)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 46), (2, 47),
(2, 8), (2, 9), (2, 10), (2, 11), (2, 12),
(2, 41), (2, 42), (2, 43), (2, 44), (2, 45);

-- 部门经理角色菜单权限(用户管理、部门管理)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 46), (3, 47),
(3, 41), (3, 42), (3, 43), (3, 44), (3, 45);

-- 普通员工角色菜单权限(查看用户、查看部门)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(4, 1), (4, 2), (4, 3), (4, 41), (4, 42);

-- 自定义权限角色菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 46), (5, 47),
(5, 41), (5, 42), (5, 43), (5, 44), (5, 45);

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