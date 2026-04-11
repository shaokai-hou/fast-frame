package com.fast.modules.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.log.entity.OperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface OperLogMapper extends BaseMapper<OperLog> {

}