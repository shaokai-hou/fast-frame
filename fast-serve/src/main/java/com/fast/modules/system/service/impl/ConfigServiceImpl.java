package com.fast.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.RedisConstants;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageRequest;
import com.fast.framework.helper.RedisHelper;
import com.fast.modules.system.domain.dto.ConfigQuery;
import com.fast.modules.system.domain.dto.ConfigVO;
import com.fast.modules.system.domain.entity.Config;
import com.fast.modules.system.mapper.ConfigMapper;
import com.fast.modules.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 参数配置Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    /**
     * 分页查询参数配置
     *
     * @param query      查询条件
     * @param pageRequest 分页参数
     * @return 参数配置分页结果
     */
    @Override
    public IPage<ConfigVO> pageConfigs(ConfigQuery query, PageRequest pageRequest) {
        return baseMapper.selectConfigPage(pageRequest.toPage(), query);
    }

    /**
     * 根据参数键名获取参数值
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    @Override
    public String getConfigValue(String configKey) {
        // 先从缓存获取
        String cacheKey = RedisConstants.CONFIG_PREFIX + configKey;
        String cached = RedisHelper.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 缓存不存在，从数据库查询
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, configKey);
        Config config = getOne(wrapper);

        if (Objects.isNull(config)) {
            throw new BusinessException("参数键名不存在");
        }
        // 写入缓存
        String configValue = config.getConfigValue();
        if (StrUtil.isNotBlank(configValue)) {
            RedisHelper.set(cacheKey, config.getConfigValue(),
                    RedisConstants.CACHE_EXPIRE_HOURS * 3600);
        }

        return configValue;
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置
     */
    @Override
    public void addConfig(Config config) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, config.getConfigKey());
        if (count(wrapper) > 0) {
            throw new BusinessException("参数键名已存在");
        }
        save(config);
        // 添加后缓存
        RedisHelper.set(RedisConstants.CONFIG_PREFIX + config.getConfigKey(),
                config.getConfigValue(), RedisConstants.CACHE_EXPIRE_HOURS * 3600
        );
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置
     */
    @Override
    public void updateConfig(Config config) {
        Config exist = getById(config.getId());
        if (exist == null) {
            throw new BusinessException("参数配置不存在");
        }
        // 系统内置参数禁止修改 configKey
        if ("0".equals(exist.getConfigType()) && !exist.getConfigKey().equals(config.getConfigKey())) {
            throw new BusinessException("系统内置参数不允许修改参数键名");
        }
        String oldKey = exist.getConfigKey();
        if (!oldKey.equals(config.getConfigKey())) {
            LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Config::getConfigKey, config.getConfigKey());
            if (count(wrapper) > 0) {
                throw new BusinessException("参数键名已存在");
            }
            // 删除旧缓存
            RedisHelper.delete(RedisConstants.CONFIG_PREFIX + oldKey);
        }
        updateById(config);
        // 更新缓存
        RedisHelper.set(RedisConstants.CONFIG_PREFIX + config.getConfigKey(),
                config.getConfigValue(), RedisConstants.CACHE_EXPIRE_HOURS * 3600
        );
    }

    /**
     * 批量删除参数配置
     *
     * @param ids 参数配置ID列表
     */
    @Override
    public void deleteConfig(List<Long> ids) {
        // 查询待删除的配置
        List<Config> configs = listByIds(ids);
        // 检查是否存在系统内置参数
        boolean hasSystemConfig = configs.stream()
                .anyMatch(c -> "0".equals(c.getConfigType()));
        if (hasSystemConfig) {
            throw new BusinessException("系统内置参数不允许删除");
        }
        // 删除前清理缓存
        for (Config config : configs) {
            RedisHelper.delete(RedisConstants.CONFIG_PREFIX + config.getConfigKey());
        }
        removeByIds(ids);
    }
}