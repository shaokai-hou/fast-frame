package com.fast.modules.log.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志导出 VO
 * 用于 Excel 导出操作日志数据
 *
 * @author fast-frame
 */
@Data
public class OperLogExportVO {

    /**
     * 日志ID
     */
    @ExcelProperty("日志ID")
    private Long id;

    /**
     * 操作模块
     */
    @ExcelProperty("操作模块")
    private String title;

    /**
     * 业务类型（数字值，对照 sys_oper_type 字典）
     */
    @ExcelProperty("业务类型")
    private Integer businessType;

    /**
     * 请求方式
     */
    @ExcelProperty("请求方式")
    private String requestMethod;

    /**
     * 操作人
     */
    @ExcelProperty("操作人")
    private String operName;

    /**
     * 操作IP
     */
    @ExcelProperty("操作IP")
    private String operIp;

    /**
     * 操作地点
     */
    @ExcelProperty("操作地点")
    private String operLocation;

    /**
     * 状态（已转换为中文：成功/失败）
     */
    @ExcelProperty("状态")
    private String status;

    /**
     * 错误消息
     */
    @ExcelProperty("错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @ExcelProperty("操作时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operTime;
}