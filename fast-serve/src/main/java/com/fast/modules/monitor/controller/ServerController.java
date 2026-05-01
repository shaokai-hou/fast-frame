package com.fast.modules.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.result.Result;
import com.fast.framework.web.BaseController;
import com.fast.modules.monitor.domain.vo.ServerVO;
import com.fast.modules.monitor.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务监控Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/monitor/server")
@RequiredArgsConstructor
public class ServerController extends BaseController {

    private final ServerService serverService;

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    @SaCheckPermission("monitor:server:detail")
    @GetMapping
    public Result<ServerVO> getInfo() {
        return success(serverService.getServerInfo());
    }
}