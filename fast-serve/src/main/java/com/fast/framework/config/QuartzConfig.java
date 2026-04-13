package com.fast.framework.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz 定时任务配置
 *
 * @author fast-frame
 */
@Configuration
public class QuartzConfig implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * ========== 模式开关：修改此处切换 ==========
     * RAM 模式：不需要 Quartz 表，任务存储在内存
     * JDBC 模式：需要执行 quartz.sql 初始化表，任务持久化到数据库
     */
    private static final boolean USE_JDBC_MODE = false;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 配置 SchedulerFactoryBean
     *
     * @param dataSource 数据源
     * @return SchedulerFactoryBean
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);

        // 设置自定义 JobFactory，支持 Spring Bean 注入
        factory.setJobFactory(springBeanJobFactory());

        // 设置 Quartz 配置属性
        factory.setQuartzProperties(quartzProperties());

        // 延迟2秒启动，等待 Spring 完成初始化
        factory.setStartupDelay(2);
        factory.setAutoStartup(true);
        factory.setOverwriteExistingJobs(true);
        factory.setApplicationContextSchedulerContextKey("applicationContext");

        return factory;
    }

    /**
     * Quartz 配置属性
     * 根据 USE_JDBC_MODE 切换 RAM 或 JDBC 模式
     *
     * @return Properties 配置
     */
    private Properties quartzProperties() {
        Properties props = new Properties();
        // 实例名称
        props.put("org.quartz.scheduler.instanceName", "FastFrameScheduler");
        // 实例ID自动生成
        props.put("org.quartz.scheduler.instanceId", "AUTO");
        // 线程池配置
        props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        props.put("org.quartz.threadPool.threadCount", "10");
        props.put("org.quartz.threadPool.threadPriority", "5");

        if (USE_JDBC_MODE) {
            // JDBC 模式：持久化到数据库
            props.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
            props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
            props.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
            props.put("org.quartz.jobStore.isClustered", "true");
            props.put("org.quartz.jobStore.clusterCheckinInterval", "15000");
            props.put("org.quartz.jobStore.misfireThreshold", "60000");
        } else {
            // RAM 模式：存储在内存（默认）
            props.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        }

        return props;
    }

    /**
     * 自定义 JobFactory
     * 支持 Quartz Job 中注入 Spring Bean
     *
     * @return SpringBeanJobFactory
     */
    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        return new SpringBeanJobFactory();
    }

    /**
     * 自定义 JobFactory 内部类
     * 实现 ApplicationContextAware 以获取 Spring 容器
     */
    private class SpringBeanJobFactory extends AdaptableJobFactory {

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            // 自动注入 Spring Bean
            AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
            beanFactory.autowireBean(job);
            return job;
        }
    }
}