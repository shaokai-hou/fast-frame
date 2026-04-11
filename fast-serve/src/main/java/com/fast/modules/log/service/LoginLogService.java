package com.fast.modules.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageResult;
import com.fast.modules.log.entity.LoginLog;

import java.util.List;

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
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 登录日志分页结果
     */
    PageResult<LoginLog> listPage(LoginLog query, Integer pageNum, Integer pageSize);

    /**
     * 清空登录日志
     */
    void clear();
}