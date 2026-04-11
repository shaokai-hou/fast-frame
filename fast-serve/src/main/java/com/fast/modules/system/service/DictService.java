package com.fast.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageResult;
import com.fast.modules.system.entity.DictType;
import com.fast.modules.system.vo.DictVO;
import com.fast.modules.system.vo.DictDataVO;

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
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 字典类型分页结果
     */
    PageResult<DictVO> listDictTypePage(DictType query, Integer pageNum, Integer pageSize);

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
     * @param dictType   字典类型
     * @param dictLabel  字典标签
     * @param dictValue  字典值
     * @param status     状态
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 字典数据分页结果
     */
    PageResult<DictDataVO> listDictDataPage(String dictType, String dictLabel, String dictValue, String status, Integer pageNum, Integer pageSize);

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
    void addDictData(com.fast.modules.system.entity.DictData dictData);

    /**
     * 修改字典数据
     *
     * @param dictData 字典数据信息
     */
    void updateDictData(com.fast.modules.system.entity.DictData dictData);

    /**
     * 删除字典数据
     *
     * @param ids 字典数据ID列表
     */
    void deleteDictData(List<Long> ids);
}