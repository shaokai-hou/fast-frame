package com.fast.modules.online.service;

import com.fast.modules.online.vo.OnlineUserVO;

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
     * @param username 用户名（可选过滤条件）
     * @return 在线用户列表
     */
    List<OnlineUserVO> listOnlineUsers(String username);

    /**
     * 强制退出
     *
     * @param tokenId Token ID
     */
    void forceLogout(String tokenId);
}