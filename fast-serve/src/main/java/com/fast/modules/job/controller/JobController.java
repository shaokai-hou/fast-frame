package com.fast.modules.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.job.domain.dto.JobDTO;
import com.fast.modules.job.domain.dto.JobQuery;
import com.fast.modules.job.domain.dto.JobVO;
import com.fast.modules.job.domain.entity.Job;
import com.fast.modules.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 定时任务Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/monitor/job")
@RequiredArgsConstructor
public class JobController extends BaseController {

    private final JobService jobService;

    /**
     * 分页查询定时任务
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 定时任务分页结果
     */
    @SaCheckPermission("monitor:job:page")
    @GetMapping("/page")
    public Result<IPage<JobVO>> page(JobQuery query, PageRequest pageRequest) {
        return success(jobService.pageJobs(query, pageRequest));
    }

    /**
     * 根据ID获取详情
     *
     * @param id 任务ID
     * @return 任务详情
     */
    @SaCheckPermission("monitor:job:detail")
    @GetMapping("/{id}")
    public Result<Job> getInfo(@PathVariable Long id) {
        return success(jobService.getById(id));
    }

    /**
     * 新增定时任务
     *
     * @param dto 任务DTO
     * @return 成功结果
     */
    @SaCheckPermission("monitor:job:add")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody JobDTO dto) {
        jobService.addJob(dto);
        return success();
    }

    /**
     * 修改定时任务
     *
     * @param dto 任务DTO
     * @return 成功结果
     */
    @SaCheckPermission("monitor:job:edit")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody JobDTO dto) {
        jobService.updateJob(dto);
        return success();
    }

    /**
     * 删除定时任务
     *
     * @param ids 任务ID数组
     * @return 成功结果
     */
    @SaCheckPermission("monitor:job:delete")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        jobService.deleteJobs(Arrays.asList(ids));
        return success();
    }

    /**
     * 切换任务状态
     *
     * @param id    任务ID
     * @param status 状态(0正常 1暂停)
     * @return 成功结果
     */
    @SaCheckPermission("monitor:job:edit")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestParam Long id, @RequestParam String status) {
        jobService.changeStatus(id, status);
        return success();
    }

    /**
     * 立即执行一次任务
     *
     * @param id 任务ID
     * @return 成功结果
     */
    @SaCheckPermission("monitor:job:edit")
    @Log(title = "定时任务", businessType = BusinessType.OTHER)
    @PutMapping("/run/{id}")
    public Result<Void> run(@PathVariable Long id) {
        jobService.runJob(id);
        return success();
    }

    /**
     * 校验Cron表达式
     *
     * @param cronExpression Cron表达式
     * @return 校验结果
     */
    @GetMapping("/cronExpression")
    public Result<Boolean> checkCronExpression(@RequestParam String cronExpression) {
        return success(jobService.checkCronExpression(cronExpression));
    }
}