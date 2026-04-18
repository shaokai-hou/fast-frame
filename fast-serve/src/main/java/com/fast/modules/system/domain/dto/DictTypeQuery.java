package com.fast.modules.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典类型查询参数
 *
 * @author fast-frame
 */
@Data
public class DictTypeQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 状态(0正常 1禁用)
     */
    private String status;
}