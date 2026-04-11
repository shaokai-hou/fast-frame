package com.fast.modules.system.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 参数配置VO
 *
 * @author fast-frame
 */
@Data
public class ConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 系统内置
     */
    private String configType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}