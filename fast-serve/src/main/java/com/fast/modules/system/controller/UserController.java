package com.fast.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.common.util.ExcelUtil;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.domain.dto.UserDTO;
import com.fast.modules.system.domain.dto.UserQuery;
import com.fast.modules.system.domain.dto.UserExportVO;
import com.fast.modules.system.domain.dto.UserImportDTO;
import com.fast.modules.system.domain.dto.UserVO;
import com.fast.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 用户分页结果
     */
    @SaCheckPermission("system:user:page")
    @GetMapping("/page")
    public Result<IPage<UserVO>> page(UserQuery query, PageRequest pageRequest) {
        return success(userService.pageUsers(query, pageRequest));
    }

    /**
     * 获取当前用户个人信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        return success(userService.getCurrentUserInfo());
    }

    /**
     * 更新当前用户个人信息
     *
     * @param dto 用户参数
     * @return 成功结果
     */
    @Log(title = "个人中心", businessType = BusinessType.UPDATE)
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserDTO dto) {
        userService.updateCurrentUserProfile(dto);
        return success();
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像信息
     */
    @Log(title = "个人中心", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public Result<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return success(userService.uploadAvatar(file));
    }

    /**
     * 修改密码
     *
     * @param dto 密码参数（包含旧密码和新密码）
     * @return 成功结果
     */
    @Log(title = "个人中心", businessType = BusinessType.UPDATE)
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody UserDTO dto) {
        userService.updateCurrentUserPassword(dto.getOldPassword(), dto.getNewPassword());
        return success();
    }

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @SaCheckPermission("system:user:detail")
    @GetMapping("/{id}")
    public Result<UserVO> getInfo(@PathVariable Long id) {
        return success(userService.getUserDetailById(id));
    }

    /**
     * 新增用户
     *
     * @param dto 用户参数
     * @return 成功结果
     */
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody UserDTO dto) {
        userService.addUser(dto);
        return success();
    }

    /**
     * 修改用户
     *
     * @param dto 用户参数
     * @return 成功结果
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody UserDTO dto) {
        userService.updateUser(dto);
        return success();
    }

    /**
     * 删除用户
     *
     * @param ids 用户ID数组
     * @return 成功结果
     */
    @SaCheckPermission("system:user:delete")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        userService.deleteUser(Arrays.asList(ids));
        return success();
    }

    /**
     * 重置密码（重置为系统初始化密码）
     *
     * @param userId 用户ID
     * @return 成功结果
     */
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd/{userId}")
    public Result<Void> resetPwd(@PathVariable Long userId) {
        userService.resetPwd(userId);
        return success();
    }

    /**
     * 修改用户状态
     *
     * @param dto 用户参数（包含用户ID和状态）
     * @return 成功结果
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody UserDTO dto) {
        userService.updateStatus(dto.getId(), dto.getStatus());
        return success();
    }

    /**
     * 导出用户列表
     *
     * @param query    查询条件
     * @param response HTTP 响应
     */
    @SaCheckPermission("system:user:export")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(UserQuery query, HttpServletResponse response) {
        List<UserExportVO> data = userService.listUserForExport(query);
        ExcelUtil.exportExcel(data, UserExportVO.class, "用户数据", response);
    }

    /**
     * 导入用户数据
     *
     * @param file Excel 文件
     * @return 导入结果
     */
    @SaCheckPermission("system:user:import")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/import")
    public Result<Map<String, Object>> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            List<UserImportDTO> dataList = ExcelUtil.importExcel(file.getInputStream(), UserImportDTO.class);
            return success(userService.importUsers(dataList));
        } catch (BusinessException e) {
            return fail(e.getMessage());
        } catch (Exception e) {
            return fail("导入失败: " + e.getMessage());
        }
    }

    /**
     * 下载用户导入模板
     *
     * @param response HTTP 响应
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(UserImportDTO.class, "用户导入", response);
    }

    /**
     * 解锁用户
     *
     * @param userId 用户ID
     * @return 成功结果
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/unlock/{userId}")
    public Result<Void> unlock(@PathVariable Long userId) {
        userService.unlockUser(userId);
        return success();
    }
}