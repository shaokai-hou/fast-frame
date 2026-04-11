package com.fast.framework.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fast.common.util.IpUtils;
import com.fast.framework.annotation.Log;
import com.fast.modules.log.domain.entity.OperLog;
import com.fast.modules.log.service.OperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志切面
 *
 * @author fast-frame
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

    private final OperLogService operLogService;

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     * @param logAnnotation 日志注解
     * @param jsonResult 方法返回结果
     */
    @AfterReturning(pointcut = "@annotation(logAnnotation)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log logAnnotation, Object jsonResult) {
        handleLog(joinPoint, logAnnotation, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param logAnnotation 日志注解
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(logAnnotation)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log logAnnotation, Exception e) {
        handleLog(joinPoint, logAnnotation, e, null);
    }

    /**
     * 处理日志记录
     *
     * @param joinPoint  切点
     * @param logAnnotation 日志注解
     * @param e          异常（可选）
     * @param jsonResult 方法返回结果（可选）
     */
    protected void handleLog(JoinPoint joinPoint, Log logAnnotation, Exception e, Object jsonResult) {
        try {
            // 获取当前用户名
            String username = StpUtil.getTokenSession().get("username", "");

            // 获取 HttpServletRequest
            HttpServletRequest request = null;
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    request = attributes.getRequest();
                }
            } catch (Exception ignored) {
            }

            // 构建日志对象
            OperLog operLog = new OperLog();
            operLog.setTitle(logAnnotation.title());
            operLog.setBusinessType(logAnnotation.businessType().getCode());
            operLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
            operLog.setOperName(username);
            operLog.setOperTime(LocalDateTime.now());
            operLog.setStatus("0");
            operLog.setTraceId(MDC.get("traceId"));

            // 设置请求信息
            if (request != null) {
                operLog.setRequestMethod(request.getMethod());
                operLog.setOperUrl(request.getRequestURI());
                operLog.setOperIp(IpUtils.getClientIp(request));
                // IP定位暂不实现
                operLog.setOperLocation("");
            }

            // 处理异常
            if (e != null) {
                operLog.setStatus("1");
                operLog.setErrorMsg(StrUtil.sub(e.getMessage(), 0, 2000));
            }

            // 保存请求参数
            if (logAnnotation.isSaveRequestData()) {
                String params = getRequestParams(joinPoint);
                operLog.setOperParam(StrUtil.sub(params, 0, 2000));
            }

            // 保存响应参数
            if (logAnnotation.isSaveResponseData() && jsonResult != null) {
                operLog.setJsonResult(StrUtil.sub(JSONUtil.toJsonStr(jsonResult), 0, 2000));
            }

            // 保存日志（异步执行，不阻塞业务线程）
            operLogService.saveAsync(operLog);
        } catch (Exception ex) {
            // 日志记录失败不影响业务
            log.error("[LogAspect] 日志记录失败", ex);
        }
    }

    /**
     * 获取请求参数
     *
     * @param joinPoint 切点
     * @return 请求参数JSON字符串
     */
    private String getRequestParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }

        // 过滤掉不需要序列化的参数
        List<Object> params = Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest)
                        && !(arg instanceof HttpServletResponse)
                        && !(arg instanceof MultipartFile)
                        && !(arg instanceof BindingResult))
                .collect(Collectors.toList());

        if (params.isEmpty()) {
            return "";
        }

        try {
            return JSONUtil.toJsonStr(params);
        } catch (Exception e) {
            return params.toString();
        }
    }
}

    