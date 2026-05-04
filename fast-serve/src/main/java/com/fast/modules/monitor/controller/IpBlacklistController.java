package com.fast.modules.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Debounce;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.monitor.domain.dto.IpBlacklistDTO;
import com.fast.modules.monitor.domain.entity.IpBlacklist;
import com.fast.modules.monitor.domain.query.IpBlacklistQuery;
import com.fast.modules.monitor.domain.vo.IpBlacklistVO;
import com.fast.modules.monitor.service.IpBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * IP黑名单Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/monitor/ipBlacklist")
@RequiredArgsConstructor
public class IpBlacklistController extends BaseController {

    private final IpBlacklistService ipBlacklistService;

    /**
     * 分页查询IP黑名单
     *
     * @param query 查询参数
     * @return 分页结果
     */
    @SaCheckPermission("monitor:ipBlacklist:page")
    @GetMapping("/page")
    public Result<IPage<IpBlacklistVO>> page(IpBlacklistQuery query) {
        return success(ipBlacklistService.pageList(query));
    }

    /**
     * 根据ID获取详情
     *
     * @param id 黑名单ID
     * @return 黑名单详情
     */
    @SaCheckPermission("monitor:ipBlacklist:page")
    @GetMapping("/{id}")
    public Result<IpBlacklist> getInfo(@PathVariable Long id) {
        return success(ipBlacklistService.getById(id));
    }

    /**
     * 新增IP黑名单
     *
     * @param dto 黑名单DTO
     * @return 成功结果
     */
    @SaCheckPermission("monitor:ipBlacklist:add")
    @Log(title = "IP黑名单", businessType = BusinessType.INSERT)
    @Debounce
    @PostMapping
    public Result<Void> add(@Validated @RequestBody IpBlacklistDTO dto) {
        ipBlacklistService.add(dto);
        return success();
    }

    /**
     * 修改IP黑名单
     *
     * @param dto 黑名单DTO
     * @return 成功结果
     */
    @SaCheckPermission("monitor:ipBlacklist:edit")
    @Log(title = "IP黑名单", businessType = BusinessType.UPDATE)
    @Debounce
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody IpBlacklistDTO dto) {
        ipBlacklistService.update(dto);
        return success();
    }

    /**
     * 删除IP黑名单
     *
     * @param ids 黑名单ID数组
     * @return 成功结果
     */
    @SaCheckPermission("monitor:ipBlacklist:delete")
    @Log(title = "IP黑名单", businessType = BusinessType.DELETE)
    @Debounce
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        ipBlacklistService.delete(Arrays.asList(ids));
        return success();
    }

    /**
     * 修改状态
     *
     * @param id    黑名单ID
     * @param status 状态
     * @return 成功结果
     */
    @SaCheckPermission("monitor:ipBlacklist:status")
    @Log(title = "IP黑名单", businessType = BusinessType.UPDATE)
    @Debounce(suffix = "status")
    @PutMapping("/status/{id}/{status}")
    public Result<Void> changeStatus(@PathVariable Long id, @PathVariable String status) {
        ipBlacklistService.changeStatus(id, status);
        return success();
    }

    /**
     * 刷新缓存
     *
     * @return 成功结果
     */
    @SaCheckPermission("monitor:ipBlacklist:refresh")
    @Log(title = "IP黑名单", businessType = BusinessType.CLEAN)
    @Debounce(suffix = "cache")
    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        ipBlacklistService.refreshCache();
        return success();
    }
}