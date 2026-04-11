package com.fast.modules.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageResult;
import com.fast.modules.log.domain.entity.OperLog;

import java.util.List;

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
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 操作日志分页结果
     */
    PageResult<OperLog> pageOperLogs(OperLog query, Integer pageNum, Integer pageSize);


    /**
     * 清空操作日志
     */
    void clear();

    /**
     * 异步保存操作日志
     * 使用独立线程池执行，不阻塞业务线程
     *
     * @param operLog 操作日志
     */
    void saveAsync(OperLog operLog);
}