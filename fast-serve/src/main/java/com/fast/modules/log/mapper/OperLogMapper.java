package com.fast.modules.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.log.domain.query.OperLogQuery;
import com.fast.modules.log.domain.vo.OperLogVO;
import com.fast.modules.log.domain.entity.OperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface OperLogMapper extends BaseMapper<OperLog> {

    /**
     * 分页查询操作日志列表
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 操作日志分页结果
     */
    IPage<OperLogVO> selectOperLogPage(IPage<OperLogVO> page, OperLogQuery query);
}