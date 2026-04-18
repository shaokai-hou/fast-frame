package com.fast.modules.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageRequest;
import com.fast.modules.log.domain.dto.LoginLogQuery;
import com.fast.modules.log.domain.dto.LoginLogVO;
import com.fast.modules.log.domain.entity.LoginLog;

/**
 * 登录日志Service
 *
 * @author fast-frame
 */
public interface LoginLogService extends IService<LoginLog> {

    /**
     * 分页查询登录日志
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 登录日志分页结果
     */
    IPage<LoginLogVO> pageLoginLogs(LoginLogQuery query, PageRequest pageRequest);
}