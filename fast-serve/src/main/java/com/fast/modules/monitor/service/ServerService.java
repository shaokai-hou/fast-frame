package com.fast.modules.monitor.service;

import com.fast.modules.monitor.domain.dto.ServerVO;

/**
 * 服务监控Service
 *
 * @author fast-frame
 */
public interface ServerService {

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    ServerVO getServerInfo();
}