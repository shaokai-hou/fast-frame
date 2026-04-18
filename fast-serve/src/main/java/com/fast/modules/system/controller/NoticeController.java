package com.fast.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.domain.dto.NoticeDTO;
import com.fast.modules.system.domain.dto.NoticeQuery;
import com.fast.modules.system.domain.dto.NoticeVO;
import com.fast.modules.system.domain.entity.Notice;
import com.fast.modules.system.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 通知公告Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
public class NoticeController extends BaseController {

    private final NoticeService noticeService;

    /**
     * 分页查询公告列表
     *
     * @param pageRequest 分页参数
     * @param query       查询条件
     * @return 公告分页结果
     */
    @SaCheckPermission("system:notice:page")
    @GetMapping("/page")
    public Result<IPage<NoticeVO>> page(PageRequest pageRequest, NoticeQuery query) {
        return success(noticeService.pageNotices(pageRequest, query));
    }

    /**
     * 根据ID获取公告详情
     *
     * @param id 公告ID
     * @return 公告详情
     */
    @SaCheckPermission("system:notice:detail")
    @GetMapping("/{id}")
    public Result<Notice> getInfo(@PathVariable String id) {
        return success(noticeService.getById(id));
    }

    /**
     * 新增公告
     *
     * @param dto 公告DTO
     * @return 成功结果
     */
    @SaCheckPermission("system:notice:add")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody NoticeDTO dto) {
        noticeService.addNotice(dto);
        return success();
    }

    /**
     * 修改公告
     *
     * @param dto 公告DTO
     * @return 成功结果
     */
    @SaCheckPermission("system:notice:edit")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody NoticeDTO dto) {
        noticeService.updateNotice(dto);
        return success();
    }

    /**
     * 删除公告
     *
     * @param ids 公告ID数组
     * @return 成功结果
     */
    @SaCheckPermission("system:notice:delete")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        noticeService.removeByIds(Arrays.asList(ids));
        return success();
    }
}