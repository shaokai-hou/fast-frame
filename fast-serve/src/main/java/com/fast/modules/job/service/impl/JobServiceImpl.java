package com.fast.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.result.PageResult;
import com.fast.modules.job.domain.dto.JobDTO;
import com.fast.modules.job.domain.dto.JobQuery;
import com.fast.modules.job.domain.entity.Job;
import com.fast.modules.job.domain.vo.JobVO;
import com.fast.modules.job.mapper.JobMapper;
import com.fast.modules.job.service.JobService;
import com.fast.modules.job.util.CronUtils;
import com.fast.modules.job.util.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务服务实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    private final Scheduler scheduler;

    @Override
    public PageResult<JobVO> pageJobs(JobQuery query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Job> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getJobName() != null, Job::getJobName, query.getJobName());
        wrapper.eq(query.getJobGroup() != null, Job::getJobGroup, query.getJobGroup());
        wrapper.eq(query.getStatus() != null, Job::getStatus, query.getStatus());
        wrapper.like(query.getInvokeTarget() != null, Job::getInvokeTarget, query.getInvokeTarget());
        wrapper.orderByDesc(Job::getCreateTime);

        Page<Job> page = page(new Page<>(pageNum, pageSize), wrapper);

        List<JobVO> voList = page.getRecords().stream().map(job -> {
            JobVO vo = new JobVO();
            BeanUtils.copyProperties(job, vo);
            // 获取下次执行时间
            if ("0".equals(job.getStatus()) && CronUtils.isValid(job.getCronExpression())) {
                java.util.Date nextTime = CronUtils.getNextExecution(job.getCronExpression());
                if (nextTime != null) {
                    vo.setNextValidTime(java.time.LocalDateTime.ofInstant(nextTime.toInstant(), java.time.ZoneId.systemDefault()));
                }
            }
            return vo;
        }).collect(java.util.stream.Collectors.toList());

        return PageResult.of(voList, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addJob(JobDTO dto) {
        // 校验Cron表达式
        if (!checkCronExpression(dto.getCronExpression())) {
            throw new RuntimeException("Cron表达式格式错误");
        }

        Job job = new Job();
        BeanUtils.copyProperties(dto, job);
        job.setStatus("0"); // 默认正常状态

        boolean success = save(job);
        if (success) {
            try {
                ScheduleUtils.createJob(scheduler, job);
            } catch (Exception e) {
                throw new RuntimeException("创建定时任务失败：" + e.getMessage());
            }
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJob(JobDTO dto) {
        // 校验Cron表达式
        if (!checkCronExpression(dto.getCronExpression())) {
            throw new RuntimeException("Cron表达式格式错误");
        }

        Job job = getById(dto.getId());
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }

        BeanUtils.copyProperties(dto, job);
        boolean success = updateById(job);
        if (success) {
            try {
                ScheduleUtils.updateJob(scheduler, job);
            } catch (Exception e) {
                throw new RuntimeException("更新定时任务失败：" + e.getMessage());
            }
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobs(List<Long> ids) {
        for (Long id : ids) {
            Job job = getById(id);
            if (job != null) {
                try {
                    ScheduleUtils.deleteJob(scheduler, id, job.getJobGroup());
                } catch (Exception e) {
                    throw new RuntimeException("删除定时任务失败：" + e.getMessage());
                }
            }
        }
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeStatus(Long id, String status) {
        Job job = getById(id);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }

        job.setStatus(status);
        boolean success = updateById(job);
        if (success) {
            try {
                if ("1".equals(status)) {
                    ScheduleUtils.pauseJob(scheduler, id, job.getJobGroup());
                } else {
                    ScheduleUtils.resumeJob(scheduler, id, job.getJobGroup());
                }
            } catch (Exception e) {
                throw new RuntimeException("切换任务状态失败：" + e.getMessage());
            }
        }
        return success;
    }

    @Override
    public boolean runJob(Long id) {
        Job job = getById(id);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }

        try {
            ScheduleUtils.runJob(scheduler, id, job.getJobGroup());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("立即执行任务失败：" + e.getMessage());
        }
    }

    @Override
    public boolean checkCronExpression(String cronExpression) {
        return CronUtils.isValid(cronExpression);
    }
}