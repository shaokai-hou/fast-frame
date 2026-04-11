package com.fast.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.RedisKeyConstants;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageResult;
import com.fast.modules.system.domain.entity.Config;
import com.fast.modules.system.mapper.ConfigMapper;
import com.fast.modules.system.service.ConfigService;
import com.fast.modules.system.domain.vo.ConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 参数配置Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存过期时间（小时）
     */
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    public PageResult<ConfigVO> pageConfigs(Config query, Integer pageNum, Integer pageSize) {
        Page<Config> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getConfigName()), Config::getConfigName, query.getConfigName())
               .like(StrUtil.isNotBlank(query.getConfigKey()), Config::getConfigKey, query.getConfigKey())
               .eq(StrUtil.isNotBlank(query.getConfigType()), Config::getConfigType, query.getConfigType())
               .orderByDesc(Config::getCreateTime);
        Page<Config> result = page(page, wrapper);
        List<ConfigVO> list = result.getRecords().stream()
                .map(config -> BeanUtil.copyProperties(config, ConfigVO.class))
                .collect(Collectors.toList());
        return PageResult.of(list, result.getTotal());
    }

    @Override
    public String getConfigValue(String configKey) {
        // 先从缓存获取
        String cacheKey = RedisKeyConstants.CONFIG_PREFIX + configKey;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached.toString();
        }

        // 缓存不存在，从数据库查询
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, configKey);
        Config config = getOne(wrapper);
        String value = config != null ? config.getConfigValue() : null;

        // 写入缓存
        if (value != null) {
            redisTemplate.opsForValue().set(cacheKey, value, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }

        return value;
    }

    @Override
    public void addConfig(Config config) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, config.getConfigKey());
        if (count(wrapper) > 0) {
            throw new BusinessException("参数键名已存在");
        }
        save(config);
        // 添加后缓存
        cacheConfigValue(config.getConfigKey(), config.getConfigValue());
    }

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
            redisTemplate.delete(RedisKeyConstants.CONFIG_PREFIX + oldKey);
        }
        updateById(config);
        // 更新缓存
        cacheConfigValue(config.getConfigKey(), config.getConfigValue());
    }

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
            redisTemplate.delete(RedisKeyConstants.CONFIG_PREFIX + config.getConfigKey());
        }
        removeByIds(ids);
    }

    /**
     * 缓存配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     */
    private void cacheConfigValue(String configKey, String configValue) {
        if (StrUtil.isNotBlank(configKey) && configValue != null) {
            redisTemplate.opsForValue().set(
                    RedisKeyConstants.CONFIG_PREFIX + configKey,
                    configValue,
                    CACHE_EXPIRE_HOURS,
                    TimeUnit.HOURS
            );
        }
    }
}