package com.fast.modules.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 状态更新DTO
 *
 * @author fast-frame
 */
@Data
public class StatusUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 状态(0正常 1禁用)
     */
    @NotBlank(message = "状态不能为空")
    private String status;
}