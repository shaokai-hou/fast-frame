package com.fast.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageRequest;
import com.fast.modules.system.domain.dto.DictDataQuery;
import com.fast.modules.system.domain.dto.DictDataVO;
import com.fast.modules.system.domain.dto.DictTypeQuery;
import com.fast.modules.system.domain.dto.DictVO;
import com.fast.modules.system.domain.entity.DictData;
import com.fast.modules.system.domain.entity.DictType;

import java.util.List;

/**
 * 字典Service
 *
 * @author fast-frame
 */
public interface DictService extends IService<DictType> {

    /**
     * 分页查询字典类型列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 字典类型分页结果
     */
    IPage<DictVO> pageDictTypes(DictTypeQuery query, PageRequest pageRequest);

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DictDataVO> listDictDataByType(String dictType);

    /**
     * 分页查询字典数据
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 字典数据分页结果
     */
    IPage<DictDataVO> pageDictData(DictDataQuery query, PageRequest pageRequest);

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型信息
     */
    void addDictType(DictType dictType);

    /**
     * 修改字典类型
     *
     * @param dictType 字典类型信息
     */
    void updateDictType(DictType dictType);

    /**
     * 删除字典类型
     *
     * @param ids 字典类型ID列表
     */
    void deleteDictType(List<Long> ids);

    /**
     * 新增字典数据
     *
     * @param dictData 字典数据信息
     */
    void addDictData(DictData dictData);

    /**
     * 修改字典数据
     *
     * @param dictData 字典数据信息
     */
    void updateDictData(DictData dictData);

    /**
     * 删除字典数据
     *
     * @param ids 字典数据ID列表
     */
    void deleteDictData(List<Long> ids);
}