package com.fast.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.domain.query.DictDataQuery;
import com.fast.modules.system.domain.vo.DictDataVO;
import com.fast.modules.system.domain.vo.DictVO;
import com.fast.modules.system.domain.entity.DictData;
import com.fast.modules.system.domain.query.DictTypeQuery;
import com.fast.modules.system.domain.entity.DictType;
import com.fast.modules.system.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 字典Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/dict")
@RequiredArgsConstructor
public class DictController extends BaseController {

    private final DictService dictService;

    /**
     * 分页查询字典类型列表
     *
     * @param query 查询条件
     * @return 字典类型分页结果
     */
    @SaCheckPermission("system:dict:page")
    @GetMapping("/type/page")
    public Result<IPage<DictVO>> pageType(DictTypeQuery query) {
        return success(dictService.pageDictTypes(query));
    }

    /**
     * 根据字典类型查询字典数据（公共接口，无需权限）
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @GetMapping("/data/{dictType}")
    public Result<List<DictDataVO>> listData(@PathVariable String dictType) {
        return success(dictService.listDictDataByType(dictType));
    }

    /**
     * 分页查询字典数据
     *
     * @param query 查询条件
     * @return 字典数据分页结果
     */
    @SaCheckPermission("system:dict:page")
    @GetMapping("/data/page")
    public Result<IPage<DictDataVO>> pageData(DictDataQuery query) {
        return success(dictService.pageDictData(query));
    }

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型信息
     * @return 成功结果
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典管理", businessType = BusinessType.INSERT)
    @PostMapping("/type")
    public Result<Void> addType(@Validated @RequestBody DictType dictType) {
        dictService.addDictType(dictType);
        return success();
    }

    /**
     * 修改字典类型
     *
     * @param dictType 字典类型信息
     * @return 成功结果
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典管理", businessType = BusinessType.UPDATE)
    @PutMapping("/type")
    public Result<Void> editType(@Validated @RequestBody DictType dictType) {
        dictService.updateDictType(dictType);
        return success();
    }

    /**
     * 删除字典类型
     *
     * @param ids 字典类型ID数组
     * @return 成功结果
     */
    @SaCheckPermission("system:dict:delete")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/type/{ids}")
    public Result<Void> removeType(@PathVariable Long[] ids) {
        dictService.deleteDictType(Arrays.asList(ids));
        return success();
    }

    /**
     * 新增字典数据
     *
     * @param dictData 字典数据信息
     * @return 成功结果
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典管理", businessType = BusinessType.INSERT)
    @PostMapping("/data")
    public Result<Void> addData(@Validated @RequestBody DictData dictData) {
        dictService.addDictData(dictData);
        return success();
    }

    /**
     * 修改字典数据
     *
     * @param dictData 字典数据信息
     * @return 成功结果
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典管理", businessType = BusinessType.UPDATE)
    @PutMapping("/data")
    public Result<Void> editData(@Validated @RequestBody DictData dictData) {
        dictService.updateDictData(dictData);
        return success();
    }

    /**
     * 删除字典数据
     *
     * @param ids 字典数据ID数组
     * @return 成功结果
     */
    @SaCheckPermission("system:dict:delete")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/data/{ids}")
    public Result<Void> removeData(@PathVariable Long[] ids) {
        dictService.deleteDictData(Arrays.asList(ids));
        return success();
    }

    /**
     * 刷新字典缓存
     *
     * @return 成功结果
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/cache")
    public Result<Void> refreshCache() {
        dictService.refreshCache();
        return success();
    }
}