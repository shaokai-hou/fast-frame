package com.fast.modules.log.controller;

import com.fast.common.result.PageResult;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.annotation.RequiresPermission;
import com.fast.common.enums.BusinessType;
import com.fast.framework.web.BaseController;
import com.fast.modules.log.entity.LoginLog;
import com.fast.modules.log.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 登录日志Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/loginlog")
@RequiredArgsConstructor
public class LoginLogController extends BaseController {

    private final LoginLogService loginLogService;

    /**
     * 分页查询登录日志
     *
     * @param query    查询条件
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 登录日志分页结果
     */
    @RequiresPermission("log:loginlog:list")
    @GetMapping("/list")
    public Result<PageResult<LoginLog>> list(LoginLog query,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(loginLogService.listPage(query, pageNum, pageSize));
    }

    /**
     * 删除登录日志
     *
     * @param ids 登录日志ID数组
     * @return 成功结果
     */
    @RequiresPermission("log:loginlog:delete")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        loginLogService.removeByIds(Arrays.asList(ids));
        return success();
    }

    /**
     * 清空登录日志
     *
     * @return 成功结果
     */
    @RequiresPermission("log:loginlog:delete")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        loginLogService.clear();
        return success();
    }
}