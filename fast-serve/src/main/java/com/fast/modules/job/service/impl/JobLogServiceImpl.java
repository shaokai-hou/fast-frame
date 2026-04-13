package com.fast.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.result.PageResult;
import com.fast.modules.job.domain.entity.JobLog;
import com.fast.modules.job.domain.vo.JobLogVO;
import com.fast.modules.job.mapper.JobLogMapper;
import com.fast.modules.job.service.JobLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务日志服务实现
 *
 * @author fast-frame
 */
@Service
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLog> implements JobLogService {

    @Override
    public PageResult<JobLogVO> pageJobLogs(String jobName, String jobGroup, String status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<JobLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(jobName != null, JobLog::getJobName, jobName);
        wrapper.eq(jobGroup != null, JobLog::getJobGroup, jobGroup);
        wrapper.eq(status != null, JobLog::getStatus, status);
        wrapper.orderByDesc(JobLog::getStartTime);

        Page<JobLog> page = page(new Page<>(pageNum, pageSize), wrapper);

        List<JobLogVO> voList = page.getRecords().stream().map(jobLog -> {
            JobLogVO vo = new JobLogVO();
            BeanUtils.copyProperties(jobLog, vo);
            // 计算耗时
            if (jobLog.getStartTime() != null && jobLog.getEndTime() != null) {
                long duration = java.time.Duration.between(jobLog.getStartTime(), jobLog.getEndTime()).toMillis();
                vo.setDuration(duration);
            }
            return vo;
        }).collect(java.util.stream.Collectors.toList());

        return PageResult.of(voList, page.getTotal());
    }

    @Override
    public void clear() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }
}