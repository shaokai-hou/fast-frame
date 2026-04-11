package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.system.domain.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * 参数配置Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

}