package com.fast.modules.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.modules.log.domain.query.LoginLogQuery;
import com.fast.modules.log.domain.vo.LoginLogExportVO;
import com.fast.modules.log.domain.vo.LoginLogVO;
import com.fast.modules.log.domain.entity.LoginLog;

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
     * @param query 查询条件
     * @return 登录日志分页结果
     */
    IPage<LoginLogVO> pageLoginLogs(LoginLogQuery query);

    /**
     * 查询登录日志列表（用于导出）
     *
     * @param query 查询条件
     * @return 登录日志导出数据列表
     */
    List<LoginLogExportVO> listLoginLogForExport(LoginLogQuery query);
}