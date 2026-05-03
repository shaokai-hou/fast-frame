package com.fast.modules.job.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.modules.job.domain.query.JobLogQuery;
import com.fast.modules.job.domain.vo.JobLogVO;
import com.fast.modules.job.domain.entity.JobLog;

/**
 * 定时任务日志服务接口
 *
 * @author fast-frame
 */
public interface JobLogService extends IService<JobLog> {

    /**
     * 分页查询任务日志
     *
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<JobLogVO> pageJobLogs(JobLogQuery query);

    /**
     * 清空任务日志
     */
    void clear();
}