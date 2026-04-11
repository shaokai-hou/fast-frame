package com.fast.framework.filter;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Trace ID 过滤器
 * 为每个请求生成唯一 Trace ID，用于全链路日志追踪
 *
 * @author fast-frame
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter implements Filter {

    /**
     * Trace ID 请求头名称
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * MDC 中的 Trace ID 键名
     */
    public static final String TRACE_ID_KEY = "traceId";

    /**
     * 过滤请求，生成并设置 Trace ID
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 生成 Trace ID
        String traceId = IdUtil.fastSimpleUUID();

        // 放入 MDC
        MDC.put(TRACE_ID_KEY, traceId);

        // 放入响应头
        httpResponse.setHeader(TRACE_ID_HEADER, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            // 清理 MDC，防止内存泄漏
            MDC.remove(TRACE_ID_KEY);
        }
    }
}