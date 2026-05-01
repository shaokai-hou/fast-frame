package com.fast.modules.log.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.log.domain.query.OperLogQuery;
import com.fast.modules.log.domain.vo.OperLogVO;
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
     * @param pageRequest 分页参数
     * @return 操作日志分页结果
     */
    @SaCheckPermission("log:operlog:page")
    @GetMapping("/page")
    public Result<IPage<OperLogVO>> page(OperLogQuery query, PageRequest pageRequest) {
        return success(operLogService.pageOperLogs(query, pageRequest));
    }

    /**
     * 根据ID获取详情
     *
     * @param id 操作日志ID
     * @return 操作日志详情
     */
    @SaCheckPermission("log:operlog:detail")
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
        operLogService.remove(new LambdaQueryWrapper<>());
        return success();
    }
}