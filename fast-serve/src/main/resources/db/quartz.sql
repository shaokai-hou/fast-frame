-- =============================================
-- Quartz 定时任务表 (PostgreSQL版本)
-- 仅 JDBC 模式需要，RAM 模式不需要执行此文件
-- =============================================

-- 删除已存在的表（按依赖顺序倒序删除）
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS CASCADE;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS CASCADE;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS CASCADE;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS CASCADE;
DROP TABLE IF EXISTS QRTZ_CALENDARS CASCADE;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS CASCADE;
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS CASCADE;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE CASCADE;
DROP TABLE IF EXISTS QRTZ_LOCKS CASCADE;
DROP TABLE IF EXISTS QRTZ_TRIGGERS CASCADE;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS CASCADE;

-- =============================================
-- Quartz 任务详情表
-- =============================================
CREATE TABLE QRTZ_JOB_DETAILS (
    sched_name VARCHAR(120) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    job_group VARCHAR(200) NOT NULL,
    description VARCHAR(250) NULL,
    job_class_name VARCHAR(250) NOT NULL,
    is_durable VARCHAR(1) NOT NULL,
    is_nonconcurrent VARCHAR(1) NOT NULL,
    is_update_data VARCHAR(1) NULL,
    requests_recovery VARCHAR(1) NULL,
    job_data BYTEA NULL,
    PRIMARY KEY (sched_name, job_name, job_group)
);

COMMENT ON TABLE QRTZ_JOB_DETAILS IS 'Quartz任务详情表';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.job_name IS '任务名称';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.job_group IS '任务分组';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.job_class_name IS '任务类名';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.is_durable IS '是否持久化';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.is_nonconcurrent IS '是否非并发';
COMMENT ON COLUMN QRTZ_JOB_DETAILS.job_data IS '任务数据';

-- =============================================
-- Quartz 触发器表
-- =============================================
CREATE TABLE QRTZ_TRIGGERS (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    job_group VARCHAR(200) NOT NULL,
    description VARCHAR(250) NULL,
    next_fire_time BIGINT NULL,
    prev_fire_time BIGINT NULL,
    priority INTEGER NULL,
    trigger_state VARCHAR(16) NOT NULL,
    trigger_type VARCHAR(8) NOT NULL,
    start_time BIGINT NOT NULL,
    end_time BIGINT NULL,
    calendar_name VARCHAR(200) NULL,
    misfire_instr SMALLINT NULL,
    job_data BYTEA NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, job_name, job_group) REFERENCES QRTZ_JOB_DETAILS(sched_name, job_name, job_group)
);

COMMENT ON TABLE QRTZ_TRIGGERS IS 'Quartz触发器表';
COMMENT ON COLUMN QRTZ_TRIGGERS.sched_name IS '调度名称';
COMMENT ON COLUMN QRTZ_TRIGGERS.trigger_name IS '触发器名称';
COMMENT ON COLUMN QRTZ_TRIGGERS.trigger_group IS '触发器分组';
COMMENT ON COLUMN QRTZ_TRIGGERS.job_name IS '任务名称';
COMMENT ON COLUMN QRTZ_TRIGGERS.job_group IS '任务分组';
COMMENT ON COLUMN QRTZ_TRIGGERS.trigger_state IS '触发器状态';
COMMENT ON COLUMN QRTZ_TRIGGERS.trigger_type IS '触发器类型';
COMMENT ON COLUMN QRTZ_TRIGGERS.next_fire_time IS '下次触发时间';
COMMENT ON COLUMN QRTZ_TRIGGERS.prev_fire_time IS '上次触发时间';

-- =============================================
-- Quartz Cron 触发器表
-- =============================================
CREATE TABLE QRTZ_CRON_TRIGGERS (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    cron_expression VARCHAR(120) NOT NULL,
    time_zone_id VARCHAR(80),
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

COMMENT ON TABLE QRTZ_CRON_TRIGGERS IS 'Quartz Cron触发器表';
COMMENT ON COLUMN QRTZ_CRON_TRIGGERS.cron_expression IS 'Cron表达式';
COMMENT ON COLUMN QRTZ_CRON_TRIGGERS.time_zone_id IS '时区ID';

-- =============================================
-- Quartz 简单触发器表
-- =============================================
CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    repeat_count BIGINT NOT NULL,
    repeat_interval BIGINT NOT NULL,
    times_triggered BIGINT NOT NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

COMMENT ON TABLE QRTZ_SIMPLE_TRIGGERS IS 'Quartz简单触发器表';
COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.repeat_count IS '重复次数';
COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.repeat_interval IS '重复间隔';
COMMENT ON COLUMN QRTZ_SIMPLE_TRIGGERS.times_triggered IS '已触发次数';

-- =============================================
-- Quartz 调度器状态表
-- =============================================
CREATE TABLE QRTZ_SCHEDULER_STATE (
    sched_name VARCHAR(120) NOT NULL,
    instance_name VARCHAR(200) NOT NULL,
    last_checkin_time BIGINT NOT NULL,
    checkin_interval BIGINT NOT NULL,
    PRIMARY KEY (sched_name, instance_name)
);

COMMENT ON TABLE QRTZ_SCHEDULER_STATE IS 'Quartz调度器状态表';
COMMENT ON COLUMN QRTZ_SCHEDULER_STATE.instance_name IS '实例名称';
COMMENT ON COLUMN QRTZ_SCHEDULER_STATE.last_checkin_time IS '上次检查时间';
COMMENT ON COLUMN QRTZ_SCHEDULER_STATE.checkin_interval IS '检查间隔';

-- =============================================
-- Quartz 锁表
-- =============================================
CREATE TABLE QRTZ_LOCKS (
    sched_name VARCHAR(120) NOT NULL,
    lock_name VARCHAR(40) NOT NULL,
    PRIMARY KEY (sched_name, lock_name)
);

COMMENT ON TABLE QRTZ_LOCKS IS 'Quartz锁表';
COMMENT ON COLUMN QRTZ_LOCKS.lock_name IS '锁名称';

-- =============================================
-- Quartz 已触发触发器表
-- =============================================
CREATE TABLE QRTZ_FIRED_TRIGGERS (
    sched_name VARCHAR(120) NOT NULL,
    entry_id VARCHAR(95) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    instance_name VARCHAR(200) NOT NULL,
    fired_time BIGINT NOT NULL,
    sched_time BIGINT NOT NULL,
    priority INTEGER NOT NULL,
    state VARCHAR(16) NOT NULL,
    job_name VARCHAR(200) NULL,
    job_group VARCHAR(200) NULL,
    is_nonconcurrent VARCHAR(1) NULL,
    requests_recovery VARCHAR(1) NULL,
    PRIMARY KEY (sched_name, entry_id)
);

COMMENT ON TABLE QRTZ_FIRED_TRIGGERS IS 'Quartz已触发触发器表';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.entry_id IS '条目ID';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.fired_time IS '触发时间';
COMMENT ON COLUMN QRTZ_FIRED_TRIGGERS.state IS '状态';

-- =============================================
-- Quartz 暂停触发器组表
-- =============================================
CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
    sched_name VARCHAR(120) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    PRIMARY KEY (sched_name, trigger_group)
);

COMMENT ON TABLE QRTZ_PAUSED_TRIGGER_GRPS IS 'Quartz暂停触发器组表';
COMMENT ON COLUMN QRTZ_PAUSED_TRIGGER_GRPS.trigger_group IS '触发器分组';

-- =============================================
-- Quartz Blob 触发器表
-- =============================================
CREATE TABLE QRTZ_BLOB_TRIGGERS (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    blob_data BYTEA NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

COMMENT ON TABLE QRTZ_BLOB_TRIGGERS IS 'Quartz Blob触发器表';
COMMENT ON COLUMN QRTZ_BLOB_TRIGGERS.blob_data IS 'Blob数据';

-- =============================================
-- Quartz 日历表
-- =============================================
CREATE TABLE QRTZ_CALENDARS (
    sched_name VARCHAR(120) NOT NULL,
    calendar_name VARCHAR(200) NOT NULL,
    calendar BYTEA NOT NULL,
    PRIMARY KEY (sched_name, calendar_name)
);

COMMENT ON TABLE QRTZ_CALENDARS IS 'Quartz日历表';
COMMENT ON COLUMN QRTZ_CALENDARS.calendar_name IS '日历名称';
COMMENT ON COLUMN QRTZ_CALENDARS.calendar IS '日历数据';

-- =============================================
-- Quartz 简单属性触发器表
-- =============================================
CREATE TABLE QRTZ_SIMPROP_TRIGGERS (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    str_prop_1 VARCHAR(512) NULL,
    str_prop_2 VARCHAR(512) NULL,
    str_prop_3 VARCHAR(512) NULL,
    int_prop_1 INTEGER NULL,
    int_prop_2 INTEGER NULL,
    long_prop_1 BIGINT NULL,
    long_prop_2 BIGINT NULL,
    dec_prop_1 NUMERIC(13,4) NULL,
    dec_prop_2 NUMERIC(13,4) NULL,
    bool_prop_1 VARCHAR(1) NULL,
    bool_prop_2 VARCHAR(1) NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS(sched_name, trigger_name, trigger_group)
);

COMMENT ON TABLE QRTZ_SIMPROP_TRIGGERS IS 'Quartz简单属性触发器表';

-- =============================================
-- Quartz 索引
-- =============================================
CREATE INDEX idx_qrtz_triggers_state ON QRTZ_TRIGGERS(sched_name, trigger_state);
CREATE INDEX idx_qrtz_triggers_next_fire_time ON QRTZ_TRIGGERS(sched_name, next_fire_time);
CREATE INDEX idx_qrtz_triggers_job_group ON QRTZ_TRIGGERS(sched_name, job_name, job_group);
CREATE INDEX idx_qrtz_fired_triggers_instance ON QRTZ_FIRED_TRIGGERS(sched_name, instance_name);
CREATE INDEX idx_qrtz_fired_triggers_job ON QRTZ_FIRED_TRIGGERS(sched_name, job_name, job_group);