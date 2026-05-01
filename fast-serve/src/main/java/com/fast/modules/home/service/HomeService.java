package com.fast.modules.home.service;

import com.fast.modules.home.domain.vo.HomeVO;

/**
 * 首页Service
 *
 * @author fast-frame
 */
public interface HomeService {

    /**
     * 获取首页数据
     *
     * @return 首页数据VO
     */
    HomeVO getHomeData();
}