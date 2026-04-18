package com.fast.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.properties.MinioProperties;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.domain.dto.FileQuery;
import com.fast.modules.system.domain.dto.FileVO;
import com.fast.modules.system.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/file")
@RequiredArgsConstructor
public class FileController extends BaseController {

    private final FileService fileService;

    /**
     * 查看头像
     * 路径: /system/file/avatar/2026/04/11/xxx.jpg
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     */
    @GetMapping("/avatar/**")
    public void viewAvatar(HttpServletRequest request, HttpServletResponse response) {
        String objectName = extractPathAfter(request, "/avatar/");
        fileService.streamFromBucket(MinioProperties.BUCKET_TYPE_AVATAR, objectName, response, false);
    }

    /**
     * 分页查询文件列表
     *
     * @param query 查询参数
     * @return 文件分页结果
     */
    @SaCheckPermission("system:file:page")
    @GetMapping("/page")
    public Result<IPage<FileVO>> pageFile(FileQuery query, PageRequest pageRequest) {
        return success(fileService.pageFiles(query, pageRequest));
    }

    /**
     * 上传文件
     *
     * @param file 上传文件
     * @return 文件信息
     */
    @SaCheckPermission("system:file:upload")
    @Log(title = "文件管理", businessType = BusinessType.INSERT)
    @PostMapping("/upload")
    public Result<FileVO> uploadFile(@RequestParam("file") MultipartFile file) {
        return success(fileService.uploadFile(file));
    }

    /**
     * 批量上传文件
     *
     * @param files 上传文件数组
     * @return 文件信息列表
     */
    @SaCheckPermission("system:file:upload")
    @Log(title = "文件管理", businessType = BusinessType.INSERT)
    @PostMapping("/uploads")
    public Result<List<FileVO>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        return success(fileService.uploadFiles(files));
    }

    /**
     * 下载文件
     * 路径: /system/file/download/2026/04/11/xxx.pdf
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     */
    @SaCheckPermission("system:file:download")
    @GetMapping("/download/**")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
        String objectName = extractPathAfter(request, "/download/");
        fileService.streamFile(objectName, response, true);
    }

    /**
     * 删除文件
     *
     * @param id 文件ID
     * @return 成功结果
     */
    @SaCheckPermission("system:file:delete")
    @Log(title = "文件管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFileById(id);
        return success();
    }

    /**
     * 从请求路径中提取指定前缀后的部分
     *
     * @param request HTTP 请求
     * @param prefix  前缀
     * @return 路径部分
     */
    private String extractPathAfter(HttpServletRequest request, String prefix) {
        String uri = request.getRequestURI();
        int idx = uri.indexOf(prefix);
        return idx >= 0 ? uri.substring(idx + prefix.length()) : "";
    }
}