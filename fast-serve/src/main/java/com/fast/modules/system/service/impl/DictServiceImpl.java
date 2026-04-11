package com.fast.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.RedisKeyConstants;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageResult;
import com.fast.modules.system.domain.entity.DictData;
import com.fast.modules.system.domain.entity.DictType;
import com.fast.modules.system.mapper.DictDataMapper;
import com.fast.modules.system.mapper.DictTypeMapper;
import com.fast.modules.system.service.DictService;
import com.fast.modules.system.domain.vo.DictDataVO;
import com.fast.modules.system.domain.vo.DictVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public PageResult<DictVO> pageDictTypes(DictType query, Integer pageNum, Integer pageSize) {
        Page<DictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getDictName()), DictType::getDictName, query.getDictName())
               .like(StrUtil.isNotBlank(query.getDictType()), DictType::getDictType, query.getDictType())
               .eq(StrUtil.isNotBlank(query.getStatus()), DictType::getStatus, query.getStatus())
               .orderByDesc(DictType::getCreateTime);
        Page<DictType> result = page(page, wrapper);
        List<DictVO> list = result.getRecords().stream()
                .map(dictType -> BeanUtil.copyProperties(dictType, DictVO.class))
                .collect(Collectors.toList());
        return PageResult.of(list, result.getTotal());
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
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
               .eq(DictData::getStatus, "0")
               .orderByAsc(DictData::getDictSort);
        List<DictDataVO> list = dictDataMapper.selectList(wrapper).stream()
                .map(data -> BeanUtil.copyProperties(data, DictDataVO.class))
                .collect(Collectors.toList());

        // 写入缓存
        redisTemplate.opsForValue().set(cacheKey, list, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);

        return list;
    }

    @Override
    public PageResult<DictDataVO> pageDictData(String dictType, String dictLabel, String dictValue, String status, Integer pageNum, Integer pageSize) {
        Page<DictData> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
               .like(StrUtil.isNotBlank(dictLabel), DictData::getDictLabel, dictLabel)
               .like(StrUtil.isNotBlank(dictValue), DictData::getDictValue, dictValue)
               .eq(StrUtil.isNotBlank(status), DictData::getStatus, status)
               .orderByAsc(DictData::getDictSort);
        Page<DictData> result = dictDataMapper.selectPage(page, wrapper);
        List<DictDataVO> list = result.getRecords().stream()
                .map(data -> BeanUtil.copyProperties(data, DictDataVO.class))
                .collect(Collectors.toList());
        return PageResult.of(list, result.getTotal());
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