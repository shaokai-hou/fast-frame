package com.fast.modules.log.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.Constants;
import com.fast.modules.log.domain.query.OperLogQuery;
import com.fast.modules.log.domain.vo.OperLogExportVO;
import com.fast.modules.log.domain.vo.OperLogVO;
import com.fast.modules.log.domain.entity.OperLog;
import com.fast.modules.log.mapper.OperLogMapper;
import com.fast.modules.log.service.OperLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
     * @param query 查询条件
     * @return 操作日志分页结果
     */
    @Override
    public IPage<OperLogVO> pageOperLogs(OperLogQuery query) {
        return baseMapper.selectOperLogPage(Page.of(query.getPageNum(), query.getPageSize()), query);
    }

    /**
     * 查询操作日志列表（用于导出）
     *
     * @param query 查询条件
     * @return 操作日志导出数据列表
     */
    @Override
    public List<OperLogExportVO> listOperLogForExport(OperLogQuery query) {
        LambdaQueryWrapper<OperLog> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(query.getTitle()), OperLog::getTitle, query.getTitle());
        wrapper.like(StringUtils.isNotBlank(query.getOperName()), OperLog::getOperName, query.getOperName());
        wrapper.eq(query.getBusinessType() != null, OperLog::getBusinessType, query.getBusinessType());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), OperLog::getStatus, query.getStatus());
        wrapper.orderByDesc(OperLog::getOperTime);

        List<OperLog> list = list(wrapper);
        return list.stream().map(operLog -> {
            OperLogExportVO vo = BeanUtil.copyProperties(operLog, OperLogExportVO.class);
            vo.setStatus(Constants.SUCCESS.equals(operLog.getStatus()) ? "成功" : "失败");
            return vo;
        }).collect(Collectors.toList());
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