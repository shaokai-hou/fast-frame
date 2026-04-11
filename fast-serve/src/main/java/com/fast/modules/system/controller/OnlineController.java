package com.fast.modules.system.controller;

import com.fast.common.result.PageResult;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.enums.BusinessType;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.service.OnlineService;
import com.fast.modules.system.domain.vo.OnlineUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/online")
@RequiredArgsConstructor
public class OnlineController extends BaseController {

    private final OnlineService onlineService;

    /**
     * 查询在线用户列表
     *
     * @param username 用户名（可选过滤条件）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 在线用户分页结果
     */
    @SaCheckPermission("system:online:list")
    @GetMapping("/list")
    public Result<PageResult<OnlineUserVO>> list(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<OnlineUserVO> list = onlineService.listOnlineUsers(username);
        return pageResult(list, list.size());
    }

    /**
     * 强制退出
     *
     * @param tokenId Token ID
     * @return 成功结果
     */
    @SaCheckPermission("system:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public Result<Void> forceLogout(@PathVariable String tokenId) {
        onlineService.forceLogout(tokenId);
        return success();
    }
}