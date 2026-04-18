package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.domain.dto.ConfigQuery;
import com.fast.modules.system.domain.dto.ConfigVO;
import com.fast.modules.system.domain.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * 参数配置Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    /**
     * 分页查询参数配置列表
     *
     * @param page  分页对象
     * @param query 查询参数
     * @return 参数配置分页结果
     */
    IPage<ConfigVO> selectConfigPage(IPage<ConfigVO> page, ConfigQuery query);
}