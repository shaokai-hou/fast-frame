package com.fast.modules.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.result.PageResult;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.common.enums.BusinessType;
import com.fast.framework.web.BaseController;
import com.fast.modules.job.domain.entity.JobLog;
import com.fast.modules.job.domain.vo.JobLogVO;
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
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     * @param status   执行状态
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 日志分页结果
     */
    @SaCheckPermission("monitor:jobLog:list")
    @GetMapping("/list")
    public Result<PageResult<JobLogVO>> list(@RequestParam(required = false) String jobName,
                                             @RequestParam(required = false) String jobGroup,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(jobLogService.pageJobLogs(jobName, jobGroup, status, pageNum, pageSize));
    }

    /**
     * 根据ID获取详情
     *
     * @param id 日志ID
     * @return 日志详情
     */
    @SaCheckPermission("monitor:jobLog:query")
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