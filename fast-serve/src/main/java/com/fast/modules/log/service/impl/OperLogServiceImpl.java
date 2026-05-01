package com.fast.modules.log.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.result.PageRequest;
import com.fast.modules.log.domain.query.OperLogQuery;
import com.fast.modules.log.domain.vo.OperLogVO;
import com.fast.modules.log.domain.entity.OperLog;
import com.fast.modules.log.mapper.OperLogMapper;
import com.fast.modules.log.service.OperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, OperLog> implements OperLogService {

    /**
     * 分页查询操作日志列表
     *
     * @param query    查询参数
     * @param pageRequest 分页参数
     * @return 操作日志分页结果
     */
    @Override
    public IPage<OperLogVO> pageOperLogs(OperLogQuery query, PageRequest pageRequest) {
        return baseMapper.selectOperLogPage(pageRequest.toPage(), query);
    }

    /**
     * 异步保存操作日志
     *
     * @param operLog 操作日志实体
     */
    @Override
    @Async("logAsyncExecutor")
    public void saveAsync(OperLog operLog) {
        try {
            save(operLog);
        } catch (Exception e) {
            log.error("异步保存操作日志失败: {}", e.getMessage());
        }
    }
}