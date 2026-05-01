package com.fast.modules.log.domain.query;

import lombok.Data;

/**
 * 操作日志查询参数
 *
 * @author fast-frame
 */
@Data
public class OperLogQuery {

    /**
     * 模块标题
     */
    private String title;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 操作类型
     */
    private Integer businessType;

    /**
     * 状态(0正常 1异常)
     */
    private String status;
}