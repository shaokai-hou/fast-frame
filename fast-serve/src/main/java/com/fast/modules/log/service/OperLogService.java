package com.fast.modules.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageRequest;
import com.fast.modules.log.domain.dto.OperLogQuery;
import com.fast.modules.log.domain.dto.OperLogVO;
import com.fast.modules.log.domain.entity.OperLog;

/**
 * 操作日志Service
 *
 * @author fast-frame
 */
public interface OperLogService extends IService<OperLog> {

    /**
     * 分页查询操作日志
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 操作日志分页结果
     */
    IPage<OperLogVO> pageOperLogs(OperLogQuery query, PageRequest pageRequest);

    /**
     * 异步保存操作日志
     * 使用独立线程池执行，不阻塞业务线程
     *
     * @param operLog 操作日志
     */
    void saveAsync(OperLog operLog);
}