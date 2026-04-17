package com.fast.modules.flow.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 流程实例启动DTO
 *
 * @author fast-frame
 */
@Data
public class FlowInstanceStartDTO {

    /**
     * 流程编码
     */
    @NotBlank(message = "流程编码不能为空")
    private String flowCode;

    /**
     * 业务ID
     */
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    /**
     * 流程变量 (JSON格式)
     */
    private String variable;
}