package com.fast.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.RedisKeyConstants;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageRequest;
import com.fast.modules.system.domain.dto.DictDataQuery;
import com.fast.modules.system.domain.dto.DictDataVO;
import com.fast.modules.system.domain.dto.DictTypeQuery;
import com.fast.modules.system.domain.dto.DictVO;
import com.fast.modules.system.domain.entity.DictData;
import com.fast.modules.system.domain.entity.DictType;
import com.fast.modules.system.mapper.DictDataMapper;
import com.fast.modules.system.mapper.DictTypeMapper;
import com.fast.modules.system.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 字典Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictService {

    private final DictDataMapper dictDataMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存过期时间（小时）
     */
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    public IPage<DictVO> pageDictTypes(DictTypeQuery query, PageRequest pageRequest) {
        return baseMapper.selectDictTypePage(pageRequest.toPage(), query);
    }

    @Override
    public List<DictDataVO> listDictDataByType(String dictType) {
        // 先从缓存获取
        String cacheKey = RedisKeyConstants.DICT_PREFIX + dictType;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (List<DictDataVO>) cached;
        }

        // 缓存不存在，从数据库查询
        List<DictDataVO> list = dictDataMapper.selectDictDataListByType(dictType);

        // 写入缓存
        redisTemplate.opsForValue().set(cacheKey, list, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);

        return list;
    }

    @Override
    public IPage<DictDataVO> pageDictData(DictDataQuery query, PageRequest pageRequest) {
        return dictDataMapper.selectDictDataPage(pageRequest.toPage(), query);
    }

    @Override
    public void addDictType(DictType dictType) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictType::getDictType, dictType.getDictType());
        if (count(wrapper) > 0) {
            throw new BusinessException("字典类型已存在");
        }
        save(dictType);
    }

    @Override
    public void updateDictType(DictType dictType) {
        DictType exist = getById(dictType.getId());
        if (exist == null) {
            throw new BusinessException("字典类型不存在");
        }
        String oldType = exist.getDictType();
        if (!oldType.equals(dictType.getDictType())) {
            LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictType::getDictType, dictType.getDictType());
            if (count(wrapper) > 0) {
                throw new BusinessException("字典类型已存在");
            }
            // 删除旧字典类型的缓存
            redisTemplate.delete(RedisKeyConstants.DICT_PREFIX + oldType);
        }
        updateById(dictType);
        // 清理新类型的缓存（内容可能变化）
        redisTemplate.delete(RedisKeyConstants.DICT_PREFIX + dictType.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(List<Long> ids) {
        for (Long id : ids) {
            DictType dictType = getById(id);
            if (dictType != null) {
                // 删除字典数据
                LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DictData::getDictType, dictType.getDictType());
                dictDataMapper.delete(wrapper);
                // 删除缓存
                redisTemplate.delete(RedisKeyConstants.DICT_PREFIX + dictType.getDictType());
            }
        }
        removeByIds(ids);
    }

    @Override
    public void addDictData(DictData dictData) {
        dictDataMapper.insert(dictData);
        // 更新缓存
        redisTemplate.delete(RedisKeyConstants.DICT_PREFIX + dictData.getDictType());
    }

    @Override
    public void updateDictData(DictData dictData) {
        dictDataMapper.updateById(dictData);
        // 更新缓存
        redisTemplate.delete(RedisKeyConstants.DICT_PREFIX + dictData.getDictType());
    }

    @Override
    public void deleteDictData(List<Long> ids) {
        // 清理相关缓存
        for (Long id : ids) {
            DictData data = dictDataMapper.selectById(id);
            if (data != null) {
                redisTemplate.delete(RedisKeyConstants.DICT_PREFIX + data.getDictType());
            }
        }
        dictDataMapper.deleteBatchIds(ids);
    }
}