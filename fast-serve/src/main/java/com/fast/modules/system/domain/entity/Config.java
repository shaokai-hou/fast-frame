package com.fast.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class Config extends BaseEntity {

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
     * 系统内置(0是 1否)
     */
    private String configType;

    /**
     * 备注
     */
    private String remark;
}