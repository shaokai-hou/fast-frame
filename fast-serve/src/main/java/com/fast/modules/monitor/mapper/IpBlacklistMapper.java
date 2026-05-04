package com.fast.modules.monitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.monitor.domain.entity.IpBlacklist;
import com.fast.modules.monitor.domain.query.IpBlacklistQuery;
import com.fast.modules.monitor.domain.vo.IpBlacklistVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * IP黑名单Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface IpBlacklistMapper extends BaseMapper<IpBlacklist> {

    /**
     * 分页查询IP黑名单
     *
     * @param page  分页参数
     * @param query 查询参数
     * @return 黑名单分页结果
     */
    IPage<IpBlacklistVO> selectPageList(IPage<IpBlacklistVO> page, @Param("query") IpBlacklistQuery query);

    /**
     * 查询所有启用的IP黑名单（用于缓存）
     *
     * @return 启用状态的黑名单列表
     */
    List<IpBlacklistVO> selectActiveList();
}