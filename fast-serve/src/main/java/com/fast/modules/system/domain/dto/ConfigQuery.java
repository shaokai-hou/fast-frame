package com.fast.modules.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 参数配置查询参数
 *
 * @author fast-frame
 */
@Data
public class ConfigQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 系统内置(0是 1否)
     */
    private String configType;
}