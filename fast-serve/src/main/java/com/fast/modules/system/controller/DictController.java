package com.fast.modules.system.controller;

import com.fast.common.result.PageResult;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.annotation.RequiresPermission;
import com.fast.common.enums.BusinessType;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.entity.DictData;
import com.fast.modules.system.entity.DictType;
import com.fast.modules.system.service.DictService;
import com.fast.modules.system.vo.DictDataVO;
import com.fast.modules.system.vo.DictVO;
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
     * @param query    查询条件
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 字典类型分页结果
     */
    @RequiresPermission("system:dict:list")
    @GetMapping("/type/list")
    public Result<PageResult<DictVO>> listType(DictType query,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(dictService.listDictTypePage(query, pageNum, pageSize));
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @RequiresPermission("system:dict:list")
    @GetMapping("/data/{dictType}")
    public Result<List<DictDataVO>> listData(@PathVariable String dictType) {
        return success(dictService.listDictDataByType(dictType));
    }

    /**
     * 分页查询字典数据
     *
     * @param dictType   字典类型
     * @param dictLabel  字典标签
     * @param dictValue  字典值
     * @param status     状态
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 字典数据分页结果
     */
    @RequiresPermission("system:dict:list")
    @GetMapping("/data/list")
    public Result<PageResult<DictDataVO>> listDataPage(
            @RequestParam String dictType,
            @RequestParam(required = false) String dictLabel,
            @RequestParam(required = false) String dictValue,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return success(dictService.listDictDataPage(dictType, dictLabel, dictValue, status, pageNum, pageSize));
    }

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型信息
     * @return 成功结果
     */
    @RequiresPermission("system:dict:add")
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
    @RequiresPermission("system:dict:edit")
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
    @RequiresPermission("system:dict:delete")
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
    @RequiresPermission("system:dict:add")
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
    @RequiresPermission("system:dict:edit")
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
    @RequiresPermission("system:dict:delete")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/data/{ids}")
    public Result<Void> removeData(@PathVariable Long[] ids) {
        dictService.deleteDictData(Arrays.asList(ids));
        return success();
    }
}