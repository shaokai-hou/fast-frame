package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.domain.dto.DictDataQuery;
import com.fast.modules.system.domain.dto.DictDataVO;
import com.fast.modules.system.domain.entity.DictData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典数据Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {

    /**
     * 分页查询字典数据列表
     *
     * @param page  分页对象
     * @param query 查询参数DTO
     * @return 字典数据分页结果
     */
    IPage<DictDataVO> selectDictDataPage(IPage<DictDataVO> page, DictDataQuery query);

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DictDataVO> selectDictDataListByType(String dictType);
}