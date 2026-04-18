package com.fast.modules.monitor.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.monitor.domain.dto.CacheQuery;
import com.fast.modules.monitor.domain.dto.CacheInfoVO;
import com.fast.modules.monitor.domain.dto.CacheKeyVO;
import com.fast.modules.monitor.domain.dto.CachePrefixVO;
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
     * 获取缓存前缀列表
     *
     * @return 缓存前缀列表
     */
    @SaCheckPermission("monitor:cache:list")
    @GetMapping("/prefixes")
    public Result<List<CachePrefixVO>> getPrefixes() {
        return success(cacheService.getCachePrefixes());
    }

    /**
     * 分页查询缓存键名列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 缓存键名分页结果
     */
    @SaCheckPermission("monitor:cache:page")
    @GetMapping("/page")
    public Result<IPage<CacheKeyVO>> page(CacheQuery query, PageRequest pageRequest) {
        return success(cacheService.pageCacheKeys(query, pageRequest));
    }

    /**
     * 获取缓存详情
     *
     * @param key 缓存键名
     * @return 缓存详情
     */
    @SaCheckPermission("monitor:cache:detail")
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
    @SaCheckPermission("monitor:cache:detail")
    @GetMapping("/redisInfo")
    public Result<String> getRedisInfo() {
        return success(cacheService.getRedisInfo());
    }
}