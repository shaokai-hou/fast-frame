package com.fast.modules.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.RedisConstants;
import com.fast.common.exception.BusinessException;
import com.fast.framework.helper.RedisHelper;
import com.fast.modules.system.domain.query.DictDataQuery;
import com.fast.modules.system.domain.vo.DictDataVO;
import com.fast.modules.system.domain.query.DictTypeQuery;
import com.fast.modules.system.domain.vo.DictVO;
import com.fast.modules.system.domain.entity.DictData;
import com.fast.modules.system.domain.entity.DictType;
import com.fast.modules.system.mapper.DictDataMapper;
import com.fast.modules.system.mapper.DictTypeMapper;
import com.fast.modules.system.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    /**
     * 分页查询字典类型
     *
     * @param query 查询条件
     * @return 字典类型分页结果
     */
    @Override
    public IPage<DictVO> pageDictTypes(DictTypeQuery query) {
        return baseMapper.selectDictTypePage(Page.of(query.getPageNum(), query.getPageSize()), query);
    }

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Override
    public List<DictDataVO> listDictDataByType(String dictType) {
        // 先从缓存获取
        String cacheKey = RedisConstants.DICT_PREFIX + dictType;
        List<DictDataVO> cached = RedisHelper.getJson(cacheKey, new TypeReference<List<DictDataVO>>() {
        });
        if (Objects.nonNull(cached)) {
            return cached;
        }
        List<DictDataVO> list = dictDataMapper.selectDictDataListByType(dictType);
        if (CollUtil.isNotEmpty(list)) {
            RedisHelper.setJson(cacheKey, list, RedisConstants.CACHE_EXPIRE_HOURS * 3600);
        }
        return list;
    }

    /**
     * 分页查询字典数据
     *
     * @param query 查询条件
     * @return 字典数据分页结果
     */
    @Override
    public IPage<DictDataVO> pageDictData(DictDataQuery query) {
        return dictDataMapper.selectDictDataPage(Page.of(query.getPageNum(), query.getPageSize()), query);
    }

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型
     */
    @Override
    public void addDictType(DictType dictType) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictType::getDictType, dictType.getDictType());
        if (exists(wrapper)) {
            throw new BusinessException("字典类型已存在");
        }
        save(dictType);
    }

    /**
     * 新增字典数据
     *
     * @param dictData 字典数据
     */
    @Override
    public void addDictData(DictData dictData) {
        dictDataMapper.insert(dictData);
        RedisHelper.delete(RedisConstants.DICT_PREFIX + dictData.getDictType());
    }

    /**
     * 修改字典类型
     *
     * @param dictType 字典类型
     */
    @Override
    public void updateDictType(DictType dictType) {
        DictType exist = getById(dictType.getId());
        if (Objects.isNull(exist)) {
            throw new BusinessException("字典类型不存在");
        }
        String oldType = exist.getDictType();
        if (!oldType.equals(dictType.getDictType())) {
            LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictType::getDictType, dictType.getDictType());
            if (exists(wrapper)) {
                throw new BusinessException("字典类型已存在");
            }
            RedisHelper.delete(RedisConstants.DICT_PREFIX + oldType);
        }
        updateById(dictType);
        RedisHelper.delete(RedisConstants.DICT_PREFIX + dictType.getDictType());
    }

    /**
     * 修改字典数据
     *
     * @param dictData 字典数据
     */
    @Override
    public void updateDictData(DictData dictData) {
        dictDataMapper.updateById(dictData);
        RedisHelper.delete(RedisConstants.DICT_PREFIX + dictData.getDictType());
    }

    /**
     * 批量删除字典类型
     *
     * @param ids 字典类型ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(List<Long> ids) {
        // 批量查询所有字典类型
        List<DictType> dictTypes = listByIds(ids);
        if (CollUtil.isEmpty(dictTypes)) {
            return;
        }

        // 收集所有字典类型键
        Set<String> dictTypeKeys = dictTypes.stream()
            .map(DictType::getDictType)
            .collect(Collectors.toSet());

        // 批量删除字典数据（一条 SQL）
        if (!dictTypeKeys.isEmpty()) {
            LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(DictData::getDictType, dictTypeKeys);
            dictDataMapper.delete(wrapper);
        }

        // 批量清理缓存
        for (String key : dictTypeKeys) {
            RedisHelper.delete(RedisConstants.DICT_PREFIX + key);
        }

        // 批量删除字典类型
        removeByIds(ids);
    }

    /**
     * 批量删除字典数据
     *
     * @param ids 字典数据ID列表
     */
    @Override
    public void deleteDictData(List<Long> ids) {
        // 清理相关缓存
        for (Long id : ids) {
            DictData data = dictDataMapper.selectById(id);
            if (Objects.nonNull(data)) {
                RedisHelper.delete(RedisConstants.DICT_PREFIX + data.getDictType());
            }
        }
        dictDataMapper.deleteBatchIds(ids);
    }

    /**
     * 刷新字典缓存
     */
    @Override
    public void refreshCache() {
        Set<String> keys = RedisHelper.scan(RedisConstants.DICT_PREFIX + "*");
        if (!keys.isEmpty()) {
            long count = RedisHelper.delete(keys);
            log.info("刷新字典缓存完成，删除 {} 个缓存Key", count);
        }
    }

    /**
     * 修改字典类型状态
     *
     * @param dictType 字典类型状态参数
     */
    @Override
    public void updateDictTypeStatus(DictType dictType) {
        DictType exist = getById(dictType.getId());
        if (Objects.isNull(exist)) {
            throw new BusinessException("字典类型不存在");
        }
        updateById(dictType);
        // 清理缓存
        RedisHelper.delete(RedisConstants.DICT_PREFIX + exist.getDictType());
    }

    /**
     * 修改字典数据状态
     *
     * @param dictData 字典数据状态参数
     */
    @Override
    public void updateDictDataStatus(DictData dictData) {
        DictData exist = dictDataMapper.selectById(dictData.getId());
        if (Objects.isNull(exist)) {
            throw new BusinessException("字典数据不存在");
        }
        dictDataMapper.updateById(dictData);
        // 清理缓存
        RedisHelper.delete(RedisConstants.DICT_PREFIX + exist.getDictType());
    }

}