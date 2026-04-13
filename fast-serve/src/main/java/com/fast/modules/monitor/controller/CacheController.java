package com.fast.modules.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.result.PageResult;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.common.enums.BusinessType;
import com.fast.framework.web.BaseController;
import com.fast.modules.monitor.domain.vo.CacheInfoVO;
import com.fast.modules.monitor.domain.vo.CacheKeyVO;
import com.fast.modules.monitor.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 缓存管理Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/monitor/cache")
@RequiredArgsConstructor
public class CacheController extends BaseController {

    private final CacheService cacheService;

    /**
     * 分页查询缓存键名列表
     *
     * @param prefix   缓存前缀（可选）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 缓存键名分页结果
     */
    @SaCheckPermission("monitor:cache:list")
    @GetMapping("/list")
    public Result<PageResult<CacheKeyVO>> list(@RequestParam(required = false) String prefix,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(cacheService.pageCacheKeys(prefix, pageNum, pageSize));
    }

    /**
     * 获取缓存详情
     *
     * @param key 缓存键名
     * @return 缓存详情
     */
    @SaCheckPermission("monitor:cache:query")
    @GetMapping("/info")
    public Result<CacheInfoVO> getInfo(@RequestParam String key) {
        return success(cacheService.getCacheInfo(key));
    }

    /**
     * 删除指定缓存
     *
     * @param key 缓存键名
     * @return 成功结果
     */
    @SaCheckPermission("monitor:cache:delete")
    @Log(title = "缓存管理", businessType = BusinessType.DELETE)
    @DeleteMapping
    public Result<Void> delete(@RequestParam String key) {
        cacheService.deleteCache(key);
        return success();
    }

    /**
     * 清空业务缓存
     *
     * @return 成功结果
     */
    @SaCheckPermission("monitor:cache:delete")
    @Log(title = "缓存管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        cacheService.clearAllCache();
        return success();
    }

    /**
     * 获取Redis信息
     *
     * @return Redis信息
     */
    @SaCheckPermission("monitor:cache:query")
    @GetMapping("/redisInfo")
    public Result<String> getRedisInfo() {
        return success(cacheService.getRedisInfo());
    }
}