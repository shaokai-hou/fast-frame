package com.fast.modules.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageResult;
import com.fast.modules.job.domain.entity.JobLog;
import com.fast.modules.job.domain.vo.JobLogVO;

/**
 * 定时任务日志服务接口
 *
 * @author fast-frame
 */
public interface JobLogService extends IService<JobLog> {

    /**
     * 分页查询任务日志
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     * @param status   执行状态
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<JobLogVO> pageJobLogs(String jobName, String jobGroup, String status, Integer pageNum, Integer pageSize);

    /**
     * 清空任务日志
     */
    void clear();
}