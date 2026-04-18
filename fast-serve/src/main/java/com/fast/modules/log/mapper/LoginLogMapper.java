package com.fast.modules.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.log.domain.dto.LoginLogQuery;
import com.fast.modules.log.domain.dto.LoginLogVO;
import com.fast.modules.log.domain.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 分页查询登录日志列表
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 登录日志分页结果
     */
    IPage<LoginLogVO> selectLoginLogPage(IPage<LoginLogVO> page, LoginLogQuery query);
}