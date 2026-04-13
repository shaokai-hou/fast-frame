package com.fast.modules.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.job.domain.entity.Job;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {
}