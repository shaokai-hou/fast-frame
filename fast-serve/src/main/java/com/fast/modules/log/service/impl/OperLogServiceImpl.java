package com.fast.modules.log.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.result.PageResult;
import com.fast.modules.log.entity.OperLog;
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

    @Override
    public PageResult<OperLog> listPage(OperLog query, Integer pageNum, Integer pageSize) {
        Page<OperLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getTitle()), OperLog::getTitle, query.getTitle())
               .like(StrUtil.isNotBlank(query.getOperName()), OperLog::getOperName, query.getOperName())
               .eq(query.getBusinessType() != null, OperLog::getBusinessType, query.getBusinessType())
               .eq(StrUtil.isNotBlank(query.getStatus()), OperLog::getStatus, query.getStatus())
               .orderByDesc(OperLog::getOperTime);
        Page<OperLog> result = page(page, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal());
    }

    @Override
    public void clear() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }

    @Override
    @Async("logAsyncExecutor")
    public void saveAsync(OperLog operLog) {
        try {
            save(operLog);
        } catch (Exception e) {
            // 异步保存失败记录日志，不影响业务
            log.error("异步保存操作日志失败: {}", e.getMessage());
        }
    }
}