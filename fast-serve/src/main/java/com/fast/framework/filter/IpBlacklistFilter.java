package com.fast.framework.filter;

import cn.hutool.extra.spring.SpringUtil;
import com.fast.common.util.IpUtils;
import com.fast.modules.monitor.service.IpBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * IP黑名单过滤器
 * 在 TraceFilter 之后执行，拦截黑名单IP的请求
 *
 * @author fast-frame
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class IpBlacklistFilter implements Filter {

    private IpBlacklistService ipBlacklistService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 延迟获取 Service，避免启动时 Bean 未初始化
        ipBlacklistService = SpringUtil.getBean(IpBlacklistService.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 获取客户端IP
        String clientIp = IpUtils.getClientIp(httpRequest);

        // 判断是否被封禁
        if (ipBlacklistService.isBlocked(clientIp)) {
            String traceId = MDC.get("traceId");
            String uri = httpRequest.getRequestURI();
            log.warn("[IpBlacklist] IP被拦截: traceId={}, ip={}, uri={}", traceId, clientIp, uri);

            // 返回 403 Forbidden
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"code\":403,\"msg\":\"您的IP被禁止访问\",\"data\":null}");
            return;
        }

        chain.doFilter(request, response);
    }
}