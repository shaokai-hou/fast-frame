package com.fast.modules.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.monitor.domain.dto.IpBlacklistDTO;
import com.fast.modules.monitor.domain.entity.IpBlacklist;
import com.fast.modules.monitor.domain.query.IpBlacklistQuery;
import com.fast.modules.monitor.domain.vo.IpBlacklistVO;

import java.util.List;

/**
 * IP黑名单Service
 *
 * @author fast-frame
 */
public interface IpBlacklistService {

    /**
     * 分页查询IP黑名单
     *
     * @param query 查询参数
     * @return 分页结果
     */
    IPage<IpBlacklistVO> pageList(IpBlacklistQuery query);

    /**
     * 根据ID获取详情
     *
     * @param id 黑名单ID
     * @return 黑名单详情
     */
    IpBlacklist getById(Long id);

    /**
     * 新增IP黑名单
     *
     * @param dto 黑名单DTO
     */
    void add(IpBlacklistDTO dto);

    /**
     * 修改IP黑名单
     *
     * @param dto 黑名单DTO
     */
    void update(IpBlacklistDTO dto);

    /**
     * 删除IP黑名单
     *
     * @param ids 黑名单ID列表
     */
    void delete(List<Long> ids);

    /**
     * 修改状态
     *
     * @param id    黑名单ID
     * @param status 状态
     */
    void changeStatus(Long id, String status);

    /**
     * 刷新缓存到Redis
     */
    void refreshCache();

    /**
     * 判断IP是否被封禁
     *
     * @param ip 待检查的IP地址
     * @return true表示被封禁，false表示正常
     */
    boolean isBlocked(String ip);
}