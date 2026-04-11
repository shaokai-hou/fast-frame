package com.fast.modules.system.controller;

import com.fast.common.result.PageResult;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.enums.BusinessType;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.domain.entity.Config;
import com.fast.modules.system.service.ConfigService;
import com.fast.modules.system.domain.vo.ConfigVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 参数配置Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class ConfigController extends BaseController {

    private final ConfigService configService;

    /**
     * 分页查询参数配置列表
     *
     * @param query    查询条件
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 配置分页结果
     */
    @SaCheckPermission("system:config:list")
    @GetMapping("/list")
    public Result<PageResult<ConfigVO>> list(Config query,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(configService.pageConfigs(query, pageNum, pageSize));
    }

    /**
     * 根据参数键名查询参数值
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    @SaCheckPermission("system:config:query")
    @GetMapping("/key/{configKey}")
    public Result<String> getByKey(@PathVariable String configKey) {
        return success(configService.getConfigValue(configKey));
    }

    /**
     * 根据ID获取参数配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    @SaCheckPermission("system:config:query")
    @GetMapping("/{id}")
    public Result<Config> getInfo(@PathVariable Long id) {
        return success(configService.getById(id));
    }

    /**
     * 新增参数配置
     *
     * @param config 配置信息
     * @return 成功结果
     */
    @SaCheckPermission("system:config:add")
    @Log(title = "参数配置", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody Config config) {
        configService.addConfig(config);
        return success();
    }

    /**
     * 修改参数配置
     *
     * @param config 配置信息
     * @return 成功结果
     */
    @SaCheckPermission("system:config:edit")
    @Log(title = "参数配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody Config config) {
        configService.updateConfig(config);
        return success();
    }

    /**
     * 删除参数配置
     *
     * @param ids 配置ID数组
     * @return 成功结果
     */
    @SaCheckPermission("system:config:delete")
    @Log(title = "参数配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        configService.deleteConfig(Arrays.asList(ids));
        return success();
    }
}