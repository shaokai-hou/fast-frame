package com.fast.modules.log.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 操作日志查询参数
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperLogQuery extends PageRequest {

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