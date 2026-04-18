package com.fast.modules.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageRequest;
import com.fast.modules.job.domain.dto.JobDTO;
import com.fast.modules.job.domain.dto.JobQuery;
import com.fast.modules.job.domain.dto.JobVO;
import com.fast.modules.job.domain.entity.Job;

import java.util.List;

/**
 * 定时任务服务接口
 *
 * @author fast-frame
 */
public interface JobService extends IService<Job> {

    /**
     * 分页查询定时任务
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    IPage<JobVO> pageJobs(JobQuery query, PageRequest pageRequest);

    /**
     * 新增定时任务
     *
     * @param dto 任务DTO
     * @return 是否成功
     */
    boolean addJob(JobDTO dto);

    /**
     * 修改定时任务
     *
     * @param dto 任务DTO
     * @return 是否成功
     */
    boolean updateJob(JobDTO dto);

    /**
     * 删除定时任务
     *
     * @param ids 任务ID列表
     * @return 是否成功
     */
    boolean deleteJobs(List<Long> ids);

    /**
     * 切换任务状态
     *
     * @param id    任务ID
     * @param status 状态(0正常 1暂停)
     * @return 是否成功
     */
    boolean changeStatus(Long id, String status);

    /**
     * 立即执行一次任务
     *
     * @param id 任务ID
     * @return 是否成功
     */
    boolean runJob(Long id);

    /**
     * 校验Cron表达式
     *
     * @param cronExpression Cron表达式
     * @return 是否有效
     */
    boolean checkCronExpression(String cronExpression);
}