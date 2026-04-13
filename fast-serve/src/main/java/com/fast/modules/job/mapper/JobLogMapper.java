package com.fast.modules.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.job.domain.entity.JobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务日志Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface JobLogMapper extends BaseMapper<JobLog> {
}