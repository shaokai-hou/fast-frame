package com.fast.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.RedisConstants;
import com.fast.common.enums.ConfigType;
import com.fast.common.exception.BusinessException;
import com.fast.framework.helper.RedisHelper;
import com.fast.modules.system.domain.entity.Config;
import com.fast.modules.system.domain.query.ConfigQuery;
import com.fast.modules.system.domain.vo.ConfigVO;
import com.fast.modules.system.mapper.ConfigMapper;
import com.fast.modules.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
     * @param query 查询条件
     * @return 参数配置分页结果
     */
    @Override
    public IPage<ConfigVO> pageConfigs(ConfigQuery query) {
        return baseMapper.selectConfigPage(Page.of(query.getPageNum(), query.getPageSize()), query);
    }

    /**
     * 根据参数键名获取参数值
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    @Override
    public String getConfigValue(String configKey) {
        String cacheKey = RedisConstants.CONFIG_PREFIX + configKey;

        // 先查缓存
        String cached = RedisHelper.get(cacheKey);
        if (StrUtil.isNotBlank(cached)) {
            return cached;
        }

        // 缓存不存在，查数据库
        Config config = getOne(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, configKey));
        if (Objects.isNull(config)) {
            throw new BusinessException("参数键名不存在");
        }

        // 写入缓存（仅缓存非空值）
        String configValue = config.getConfigValue();
        if (StrUtil.isNotBlank(configValue)) {
            RedisHelper.set(cacheKey, configValue, RedisConstants.CACHE_EXPIRE_HOURS * 3600);
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
        if (exists(wrapper)) {
            throw new BusinessException("参数键名已存在");
        }
        save(config);
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置
     */
    @Override
    public void updateConfig(Config config) {
        Config exist = getById(config.getId());
        if (Objects.isNull(exist)) {
            throw new BusinessException("参数配置不存在");
        }

        String oldKey = exist.getConfigKey();
        String newKey = config.getConfigKey();
        boolean keyChanged = !oldKey.equals(newKey);

        // 系统内置参数禁止修改 configKey
        if (ConfigType.SYSTEM.getCode().equals(exist.getConfigType()) && keyChanged) {
            throw new BusinessException("系统内置参数不允许修改参数键名");
        }

        // configKey 变化时检查是否重复
        if (keyChanged && exists(new LambdaQueryWrapper<Config>().eq(Config::getConfigKey, newKey))) {
            throw new BusinessException("参数键名已存在");
        }

        // 删除缓存
        RedisHelper.delete(RedisConstants.CONFIG_PREFIX + oldKey);
        updateById(config);
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
                .anyMatch(c -> ConfigType.SYSTEM.getCode().equals(c.getConfigType()));
        if (hasSystemConfig) {
            throw new BusinessException("系统内置参数不允许删除");
        }
        // 删除前清理缓存
        for (Config config : configs) {
            RedisHelper.delete(RedisConstants.CONFIG_PREFIX + config.getConfigKey());
        }
        removeByIds(ids);
    }

    /**
     * 刷新参数配置缓存
     */
    @Override
    public void refreshCache() {
        Set<String> keys = RedisHelper.scan(RedisConstants.CONFIG_PREFIX + "*");
        if (!keys.isEmpty()) {
            long count = RedisHelper.delete(keys);
            log.info("刷新参数配置缓存完成，删除 {} 个缓存Key", count);
        }
    }
}