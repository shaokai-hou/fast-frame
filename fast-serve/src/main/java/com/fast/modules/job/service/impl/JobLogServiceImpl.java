package com.fast.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.modules.job.domain.query.JobLogQuery;
import com.fast.modules.job.domain.vo.JobLogVO;
import com.fast.modules.job.domain.entity.JobLog;
import com.fast.modules.job.mapper.JobLogMapper;
import com.fast.modules.job.service.JobLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时任务日志服务实现
 *
 * @author fast-frame
 */
@Service
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLog> implements JobLogService {

    @Override
    public IPage<JobLogVO> pageJobLogs(JobLogQuery query) {
        LambdaQueryWrapper<JobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getJobName() != null, JobLog::getJobName, query.getJobName());
        wrapper.eq(query.getJobGroup() != null, JobLog::getJobGroup, query.getJobGroup());
        wrapper.eq(query.getStatus() != null, JobLog::getStatus, query.getStatus());
        wrapper.orderByDesc(JobLog::getStartTime);

        Page<JobLog> page = page(Page.of(query.getPageNum(), query.getPageSize()), wrapper);

        List<JobLogVO> voList = page.getRecords().stream().map(jobLog -> {
            JobLogVO vo = new JobLogVO();
            BeanUtils.copyProperties(jobLog, vo);
            // 计算耗时
            if (jobLog.getStartTime() != null && jobLog.getEndTime() != null) {
                long duration = Duration.between(jobLog.getStartTime(), jobLog.getEndTime()).toMillis();
                vo.setDuration(duration);
            }
            return vo;
        }).collect(Collectors.toList());

        Page<JobLogVO> resultPage = Page.of(query.getPageNum(), query.getPageSize());
        resultPage.setRecords(voList);
        resultPage.setTotal(page.getTotal());
        return resultPage;
    }

    @Override
    public void clear() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }
}