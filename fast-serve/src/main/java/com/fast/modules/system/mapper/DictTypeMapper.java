package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.domain.query.DictTypeQuery;
import com.fast.modules.system.domain.vo.DictVO;
import com.fast.modules.system.domain.entity.DictType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典类型Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface DictTypeMapper extends BaseMapper<DictType> {

    /**
     * 分页查询字典类型列表
     *
     * @param page  分页对象
     * @param query 查询参数
     * @return 字典类型分页结果
     */
    IPage<DictVO> selectDictTypePage(IPage<DictVO> page, DictTypeQuery query);
}