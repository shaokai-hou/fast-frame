package com.fast.framework.config;

import com.fast.framework.filter.IpBlacklistFilter;
import com.fast.framework.filter.TraceFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Filter 配置类
 * 注册 TraceFilter 和 IpBlacklistFilter
 *
 * @author fast-frame
 */
@Configuration
public class FilterConfig {

    /**
     * 注册 TraceFilter
     * 设置最高优先级，确保最先执行
     *
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterRegistration() {
        FilterRegistrationBean<TraceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceFilter());
        registration.addUrlPatterns("/*");
        registration.setName("traceFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    /**
     * 注册 IpBlacklistFilter
     * 在 TraceFilter 之后执行
     *
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<IpBlacklistFilter> ipBlacklistFilterRegistration() {
        FilterRegistrationBean<IpBlacklistFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new IpBlacklistFilter());
        registration.addUrlPatterns("/*");
        registration.setName("ipBlacklistFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }
}