package com.fast.modules.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据查询参数
 *
 * @author fast-frame
 */
@Data
public class DictDataQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 状态
     */
    private String status;
}