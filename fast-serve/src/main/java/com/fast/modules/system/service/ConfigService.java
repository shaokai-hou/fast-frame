package com.fast.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageRequest;
import com.fast.modules.system.domain.dto.ConfigQuery;
import com.fast.modules.system.domain.dto.ConfigVO;
import com.fast.modules.system.domain.entity.Config;

import java.util.List;

/**
 * 参数配置Service
 *
 * @author fast-frame
 */
public interface ConfigService extends IService<Config> {

    /**
     * 分页查询参数配置列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 配置分页结果
     */
    IPage<ConfigVO> pageConfigs(ConfigQuery query, PageRequest pageRequest);

    /**
     * 根据参数键名查询参数值
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    String getConfigValue(String configKey);

    /**
     * 新增参数配置
     *
     * @param config 配置信息
     */
    void addConfig(Config config);

    /**
     * 修改参数配置
     *
     * @param config 配置信息
     */
    void updateConfig(Config config);

    /**
     * 删除参数配置
     *
     * @param ids 配置ID列表
     */
    void deleteConfig(List<Long> ids);
}