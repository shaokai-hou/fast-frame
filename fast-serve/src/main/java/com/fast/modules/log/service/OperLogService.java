package com.fast.modules.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageRequest;
import com.fast.modules.log.domain.query.OperLogQuery;
import com.fast.modules.log.domain.vo.OperLogExportVO;
import com.fast.modules.log.domain.vo.OperLogVO;
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
     * @param pageRequest 分页参数
     * @return 操作日志分页结果
     */
    IPage<OperLogVO> pageOperLogs(OperLogQuery query, PageRequest pageRequest);

    /**
     * 查询操作日志列表（用于导出）
     *
     * @param query 查询条件
     * @return 操作日志导出数据列表
     */
    List<OperLogExportVO> listOperLogForExport(OperLogQuery query);

    /**
     * 异步保存操作日志
     * 使用独立线程池执行，不阻塞业务线程
     *
     * @param operLog 操作日志
     */
    void saveAsync(OperLog operLog);
}