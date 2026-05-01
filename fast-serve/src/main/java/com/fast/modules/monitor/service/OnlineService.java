package com.fast.modules.monitor.service;

import com.fast.modules.monitor.domain.query.OnlineUserQuery;
import com.fast.modules.monitor.domain.vo.OnlineUserVO;

import java.util.List;

/**
 * 在线用户Service
 *
 * @author fast-frame
 */
public interface OnlineService {

    /**
     * 查询在线用户列表
     *
     * @param query    查询条件
     * @return 在线用户列表
     */
    List<OnlineUserVO> listOnlineUsers(OnlineUserQuery query);

    /**
     * 强制退出
     *
     * @param tokenId Token ID
     */
    void forceLogout(String tokenId);
}