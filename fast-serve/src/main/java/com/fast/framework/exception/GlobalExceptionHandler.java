package com.fast.framework.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author fast-frame
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     *
     * @param e 业务异常
     * @return 失败结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("[TraceId: {}] 业务异常: {}", MDC.get("traceId"), e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * Sa-Token 未登录异常
     *
     * @param e 未登录异常
     * @return 失败结果
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        log.error("[TraceId: {}] 未登录异常: {}", MDC.get("traceId"), e.getMessage());
        String message;
        String type = e.getType();
        if ("-5".equals(type)) {
            message = "您已被管理员踢下线";
        } else if ("-4".equals(type)) {
            message = "您的账号已在其他设备登录，当前设备已被下线";
        } else {
            message = "未登录或登录已过期";
        }
        return Result.fail(401, message);
    }

    /**
     * Sa-Token 无权限异常
     *
     * @param e 无权限异常
     * @return 失败结果
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handleNotPermissionException(NotPermissionException e) {
        log.error("[TraceId: {}] 无权限异常: {}", MDC.get("traceId"), e.getMessage());
        return Result.fail(403, "无权限访问");
    }

    /**
     * Sa-Token 无角色异常
     *
     * @param e 无角色异常
     * @return 失败结果
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<Void> handleNotRoleException(NotRoleException e) {
        log.error("[TraceId: {}] 无角色异常: {}", MDC.get("traceId"), e.getMessage());
        return Result.fail(403, "无角色权限");
    }

    /**
     * 参数校验异常 - @RequestBody
     *
     * @param e 参数校验异常
     * @return 失败结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("[TraceId: {}] 参数校验异常: {}", MDC.get("traceId"), message);
        return Result.fail(400, message);
    }

    /**
     * 参数校验异常 - @ModelAttribute
     *
     * @param e 参数绑定异常
     * @return 失败结果
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("[TraceId: {}] 参数绑定异常: {}", MDC.get("traceId"), message);
        return Result.fail(400, message);
    }

    /**
     * 参数校验异常 - @RequestParam
     *
     * @param e 约束违反异常
     * @return 失败结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.error("[TraceId: {}] 约束违反异常: {}", MDC.get("traceId"), message);
        return Result.fail(400, message);
    }

    /**
     * 运行时异常
     *
     * @param e 运行时异常
     * @return 失败结果
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("[TraceId: {}] 运行时异常: {}", MDC.get("traceId"), e.getMessage(), e);
        return Result.fail("系统异常，请联系管理员");
    }

    /**
     * 系统异常
     *
     * @param e 系统异常
     * @return 失败结果
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("[TraceId: {}] 系统异常: {}", MDC.get("traceId"), e.getMessage(), e);
        return Result.fail("系统异常，请联系管理员");
    }
}