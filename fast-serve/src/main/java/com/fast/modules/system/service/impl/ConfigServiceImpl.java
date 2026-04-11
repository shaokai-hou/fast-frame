package com.fast.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageResult;
import com.fast.modules.system.entity.Config;
import com.fast.modules.system.mapper.ConfigMapper;
import com.fast.modules.system.service.ConfigService;
import com.fast.modules.system.vo.ConfigVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数配置Service实现
 *
 * @author fast-frame
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    @Override
    public PageResult<ConfigVO> listConfigPage(Config query, Integer pageNum, Integer pageSize) {
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
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, configKey);
        Config config = getOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public void addConfig(Config config) {
        LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Config::getConfigKey, config.getConfigKey());
        if (count(wrapper) > 0) {
            throw new BusinessException("参数键名已存在");
        }
        save(config);
    }

    @Override
    public void updateConfig(Config config) {
        Config exist = getById(config.getId());
        if (exist == null) {
            throw new BusinessException("参数配置不存在");
        }
        if (!exist.getConfigKey().equals(config.getConfigKey())) {
            LambdaQueryWrapper<Config> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Config::getConfigKey, config.getConfigKey());
            if (count(wrapper) > 0) {
                throw new BusinessException("参数键名已存在");
            }
        }
        updateById(config);
    }

    @Override
    public void deleteConfig(List<Long> ids) {
        removeByIds(ids);
    }
}