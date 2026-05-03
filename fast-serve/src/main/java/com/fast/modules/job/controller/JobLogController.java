package com.fast.modules.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.job.domain.query.JobLogQuery;
import com.fast.modules.job.domain.vo.JobLogVO;
import com.fast.modules.job.domain.entity.JobLog;
import com.fast.modules.job.service.JobLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 定时任务日志Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/monitor/jobLog")
@RequiredArgsConstructor
public class JobLogController extends BaseController {

    private final JobLogService jobLogService;

    /**
     * 分页查询任务日志
     *
     * @param query 查询条件
     * @return 日志分页结果
     */
    @SaCheckPermission("monitor:jobLog:page")
    @GetMapping("/page")
    public Result<IPage<JobLogVO>> page(JobLogQuery query) {
        return success(jobLogService.pageJobLogs(query));
    }

    /**
     * 根据ID获取详情
     *
     * @param id 日志ID
     * @return 日志详情
     */
    @SaCheckPermission("monitor:jobLog:detail")
    @GetMapping("/{id}")
    public Result<JobLog> getInfo(@PathVariable Long id) {
        return success(jobLogService.getById(id));
    }

    /**
     * 删除任务日志
     *
     * @param ids 日志ID数组
     * @return 成功结果
     */
    @SaCheckPermission("monitor:jobLog:delete")
    @Log(title = "任务日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        jobLogService.removeByIds(Arrays.asList(ids));
        return success();
    }

    /**
     * 清空任务日志
     *
     * @return 成功结果
     */
    @SaCheckPermission("monitor:jobLog:delete")
    @Log(title = "任务日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        jobLogService.clear();
        return success();
    }
}