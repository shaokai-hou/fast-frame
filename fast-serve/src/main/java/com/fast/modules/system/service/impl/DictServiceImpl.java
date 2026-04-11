package com.fast.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageResult;
import com.fast.modules.system.entity.DictData;
import com.fast.modules.system.entity.DictType;
import com.fast.modules.system.mapper.DictDataMapper;
import com.fast.modules.system.mapper.DictTypeMapper;
import com.fast.modules.system.service.DictService;
import com.fast.modules.system.vo.DictDataVO;
import com.fast.modules.system.vo.DictVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictService {

    private final DictDataMapper dictDataMapper;

    @Override
    public PageResult<DictVO> listDictTypePage(DictType query, Integer pageNum, Integer pageSize) {
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
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
               .eq(DictData::getStatus, "0")
               .orderByAsc(DictData::getDictSort);
        return dictDataMapper.selectList(wrapper).stream()
                .map(data -> BeanUtil.copyProperties(data, DictDataVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<DictDataVO> listDictDataPage(String dictType, String dictLabel, String dictValue, String status, Integer pageNum, Integer pageSize) {
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
        if (!exist.getDictType().equals(dictType.getDictType())) {
            LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictType::getDictType, dictType.getDictType());
            if (count(wrapper) > 0) {
                throw new BusinessException("字典类型已存在");
            }
        }
        updateById(dictType);
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
            }
        }
        removeByIds(ids);
    }

    @Override
    public void addDictData(DictData dictData) {
        dictDataMapper.insert(dictData);
    }

    @Override
    public void updateDictData(DictData dictData) {
        dictDataMapper.updateById(dictData);
    }

    @Override
    public void deleteDictData(List<Long> ids) {
        dictDataMapper.deleteBatchIds(ids);
    }
}