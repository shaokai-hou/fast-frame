package com.fast.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.domain.dto.OnlineUserQuery;
import com.fast.modules.system.domain.dto.OnlineUserVO;
import com.fast.modules.system.service.OnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 查询在线用户列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 在线用户分页结果
     */
    @SaCheckPermission("monitor:online:page")
    @GetMapping("/page")
    public Result<IPage<OnlineUserVO>> page(OnlineUserQuery query, PageRequest pageRequest) {
        List<OnlineUserVO> list = onlineService.listOnlineUsers(query);
        Page<OnlineUserVO> resultPage = pageRequest.toPage();
        resultPage.setRecords(list);
        resultPage.setTotal(list.size());
        return success(resultPage);
    }

    /**
     * 强制退出
     *
     * @param tokenId Token ID
     * @return 成功结果
     */
    @SaCheckPermission("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public Result<Void> forceLogout(@PathVariable String tokenId) {
        onlineService.forceLogout(tokenId);
        return success();
    }
}