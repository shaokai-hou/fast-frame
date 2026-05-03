package com.fast.modules.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.monitor.domain.query.OnlineUserQuery;
import com.fast.modules.monitor.domain.vo.OnlineUserVO;
import com.fast.modules.monitor.service.OnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 在线用户Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/monitor/online")
@RequiredArgsConstructor
public class OnlineController extends BaseController {

    private final OnlineService onlineService;

    /**
     * 分页查询在线用户列表
     *
     * @param query      查询条件
     * @param pageRequest 分页参数
     * @return 在线用户分页结果
     */
    @SaCheckPermission("monitor:online:page")
    @GetMapping("/page")
    public Result<IPage<OnlineUserVO>> page(OnlineUserQuery query, PageRequest pageRequest) {
        return success(onlineService.pageOnlineUsers(query, pageRequest));
    }

    /**
     * 强制退出
     *
     * @param userId 用户ID
     * @return 成功结果
     */
    @SaCheckPermission("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{userId}")
    public Result<Void> forceLogout(@PathVariable Long userId) {
        StpUtil.kickout(userId);
        return success();
    }
}