package com.fast.modules.home.controller;

import com.fast.common.result.Result;
import com.fast.framework.web.BaseController;
import com.fast.modules.home.domain.dto.HomeVO;
import com.fast.modules.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController extends BaseController {

    private final HomeService homeService;

    /**
     * 获取首页数据
     *
     * @return 首页数据
     */
    @GetMapping("/data")
    public Result<HomeVO> getHomeData() {
        return success(homeService.getHomeData());
    }
}