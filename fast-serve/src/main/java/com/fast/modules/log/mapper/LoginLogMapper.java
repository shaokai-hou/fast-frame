package com.fast.modules.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.log.domain.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

}