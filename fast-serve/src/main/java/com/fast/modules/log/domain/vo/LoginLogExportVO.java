package com.fast.modules.log.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志导出 VO
 * 用于 Excel 导出登录日志数据
 *
 * @author fast-frame
 */
@Data
public class LoginLogExportVO {

    /**
     * 访问ID
     */
    @ExcelProperty("访问ID")
    private Long id;

    /**
     * 用户名
     */
    @ExcelProperty("用户名")
    private String username;

    /**
     * IP地址
     */
    @ExcelProperty("IP地址")
    private String ipAddress;

    /**
     * 登录地点
     */
    @ExcelProperty("登录地点")
    private String loginLocation;

    /**
     * 浏览器
     */
    @ExcelProperty("浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @ExcelProperty("操作系统")
    private String os;

    /**
     * 状态（已转换为中文：成功/失败）
     */
    @ExcelProperty("状态")
    private String status;

    /**
     * 提示消息
     */
    @ExcelProperty("提示消息")
    private String msg;

    /**
     * 登录时间
     */
    @ExcelProperty("登录时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;
}