package com.fast.modules.log.controller;

import com.fast.common.result.PageResult;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.enums.BusinessType;
import com.fast.framework.web.BaseController;
import com.fast.modules.log.domain.entity.OperLog;
import com.fast.modules.log.service.OperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 操作日志Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/operlog")
@RequiredArgsConstructor
public class OperLogController extends BaseController {

    private final OperLogService operLogService;

    /**
     * 分页查询操作日志
     *
     * @param query    查询条件
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 操作日志分页结果
     */
    @SaCheckPermission("log:operlog:list")
    @GetMapping("/list")
    public Result<PageResult<OperLog>> list(OperLog query,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(operLogService.pageOperLogs(query, pageNum, pageSize));
    }

    /**
     * 根据ID获取详情
     *
     * @param id 操作日志ID
     * @return 操作日志详情
     */
    @SaCheckPermission("log:operlog:query")
    @GetMapping("/{id}")
    public Result<OperLog> getInfo(@PathVariable Long id) {
        return success(operLogService.getById(id));
    }

    /**
     * 删除操作日志
     *
     * @param ids 操作日志ID数组
     * @return 成功结果
     */
    @SaCheckPermission("log:operlog:delete")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        operLogService.removeByIds(Arrays.asList(ids));
        return success();
    }

    /**
     * 清空操作日志
     *
     * @return 成功结果
     */
    @SaCheckPermission("log:operlog:delete")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        operLogService.clear();
        return success();
    }
}