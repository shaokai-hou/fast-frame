package com.fast.modules.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.monitor.domain.query.OnlineUserQuery;
import com.fast.modules.monitor.domain.vo.OnlineUserVO;

/**
 * 在线用户Service
 *
 * @author fast-frame
 */
public interface OnlineService {

    /**
     * 分页查询在线用户列表
     *
     * @param query 查询条件
     * @return 在线用户分页结果
     */
    IPage<OnlineUserVO> pageOnlineUsers(OnlineUserQuery query);
}