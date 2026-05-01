package com.fast.modules.log.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.log.domain.query.LoginLogQuery;
import com.fast.modules.log.domain.vo.LoginLogVO;
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
     * @param query       查询条件
     * @param pageRequest 分页参数
     * @return 登录日志分页结果
     */
    @SaCheckPermission("log:loginlog:page")
    @GetMapping("/page")
    public Result<IPage<LoginLogVO>> page(LoginLogQuery query, PageRequest pageRequest) {
        return success(loginLogService.pageLoginLogs(query, pageRequest));
    }

    /**
     * 删除登录日志
     *
     * @param ids 登录日志ID数组
     * @return 成功结果
     */
    @SaCheckPermission("log:loginlog:delete")
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
    @SaCheckPermission("log:loginlog:delete")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        loginLogService.remove(Wrappers.emptyWrapper());
        return success();
    }
}