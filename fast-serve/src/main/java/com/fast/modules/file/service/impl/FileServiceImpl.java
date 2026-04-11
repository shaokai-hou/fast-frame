package com.fast.modules.file.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageResult;
import com.fast.framework.properties.MinioProperties;
import com.fast.modules.file.domain.dto.FileQuery;
import com.fast.modules.file.domain.entity.File;
import com.fast.modules.file.mapper.FileMapper;
import com.fast.modules.file.service.FileService;
import com.fast.modules.file.domain.vo.FileVO;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * 文件Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileMapper fileMapper;

    /**
     * 允许的文件扩展名白名单
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            // 文档类型
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md",
            // 图片类型
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg",
            // 压缩类型
            "zip", "rar", "7z", "tar", "gz",
            // 音频视频
            "mp3", "mp4", "avi", "mov", "wav", "flv"
    ));

    /**
     * 最大文件大小（字节）：50MB
     */
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * 头像最大文件大小（字节）：5MB
     */
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;

    /**
     * 允许的图片扩展名（头像上传）
     */
    private static final Set<String> ALLOWED_AVATAR_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    /**
     * 应用启动后初始化桶
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        initBuckets();
    }


    /**
     * 分页查询文件列表
     *
     * @param query 查询参数
     * @return 文件分页结果
     */
    @Override
    public PageResult<FileVO> pageFiles(FileQuery query) {
        Page<File> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getFileName()), File::getFileName, query.getFileName());
        wrapper.eq(StrUtil.isNotBlank(query.getBucketType()), File::getBucketType, query.getBucketType());
        wrapper.orderByDesc(File::getCreateTime);
        Page<File> result = fileMapper.selectPage(page, wrapper);
        List<FileVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal());
    }


    /**
     * 上传文件（默认 file 桶）
     *
     * @param file 上传的文件
     * @return 文件信息 VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO uploadFile(MultipartFile file) {
        validateFile(file, false);
        return uploadToBucket(file, MinioProperties.BUCKET_TYPE_FILE);
    }

    /**
     * 批量上传文件（默认 file 桶）
     *
     * @param files 上传的文件数组
     * @return 文件信息列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileVO> uploadFiles(MultipartFile[] files) {
        return uploadFilesToBucket(files, MinioProperties.BUCKET_TYPE_FILE);
    }

    /**
     * 上传文件到指定桶
     * <p>
     * 生成 UUID 文件名，按日期分目录存储，并记录到数据库
     *
     * @param file       上传的文件
     * @param bucketType 桶类型（avatar/file）
     * @return 文件信息 VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO uploadToBucket(MultipartFile file, String bucketType) {
        // 验证文件
        boolean isAvatar = MinioProperties.BUCKET_TYPE_AVATAR.equals(bucketType);
        validateFile(file, isAvatar);

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.extName(originalFilename);

            // 使用安全的文件名：UUID + 验证后的扩展名
            String safeExtension = sanitizeExtension(extension);
            String fileName = IdUtil.fastSimpleUUID() + "." + safeExtension;

            // 按日期分目录存储
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String objectName = datePath + "/" + fileName;

            // 获取实际桶名称
            String bucketName = minioProperties.getBucketName(bucketType);

            // 上传文件到 MinIO
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            inputStream.close();

            // 记录到数据库（仅 file 桶记录）
            if (MinioProperties.BUCKET_TYPE_FILE.equals(bucketType)) {
                File fileEntity = new File();
                fileEntity.setFileName(objectName);
                fileEntity.setOriginalFilename(originalFilename);
                fileEntity.setBucketType(bucketType);
                fileEntity.setContentType(file.getContentType());
                fileEntity.setFileSize(file.getSize());
                fileMapper.insert(fileEntity);
            }

            // 返回文件信息
            FileVO vo = new FileVO();
            vo.setId(MinioProperties.BUCKET_TYPE_FILE.equals(bucketType) ? fileMapper.selectOne(
                    new LambdaQueryWrapper<File>().eq(File::getFileName, objectName)).getId() : null);
            vo.setBucket(bucketType);
            vo.setFileName(objectName);
            vo.setOriginalFilename(originalFilename);
            vo.setSize(file.getSize());
            vo.setContentType(file.getContentType());
            vo.setUploadTime(LocalDateTime.now());
            return vo;
        } catch (Exception e) {
            log.error("上传文件失败: {}", e.getMessage());
            throw new BusinessException("上传文件失败: " + e.getMessage());
        }
    }

    /**
     * 批量上传文件到指定桶
     *
     * @param files      上传的文件数组
     * @param bucketType 桶类型（avatar/file）
     * @return 文件信息列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileVO> uploadFilesToBucket(MultipartFile[] files, String bucketType) {
        List<FileVO> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(uploadToBucket(file, bucketType));
        }
        return result;
    }


    /**
     * 根据ID删除文件
     * <p>
     * 同步删除 MinIO 文件和数据库记录
     *
     * @param id 文件ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileById(Long id) {
        File file = fileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        // 删除 MinIO 文件
        deleteFromBucket(file.getBucketType(), file.getFileName());
        // 删除数据库记录
        fileMapper.deleteById(id);
    }

    /**
     * 删除文件
     *
     * @param bucketType 桶类型（avatar/file）
     * @param objectName 文件对象名称（路径）
     */
    @Override
    public void deleteFromBucket(String bucketType, String objectName) {
        try {
            String bucketName = minioProperties.getBucketName(bucketType);
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage());
            throw new BusinessException("删除文件失败: " + e.getMessage());
        }
    }


    /**
     * 初始化桶
     * <p>
     * 创建 avatar 和 file 桶
     */
    @Override
    public void initBuckets() {
        try {
            String avatarBucket = minioProperties.getAvatarBucket();
            createBucket(avatarBucket);

            String fileBucket = minioProperties.getFileBucket();
            createBucket(fileBucket);
        } catch (Exception e) {
            log.error("初始化MinIO桶失败: {}", e.getMessage());
        }
    }

    /**
     * 流式输出文件（默认 file 桶）
     *
     * @param objectName 文件对象名称（路径）
     * @param response   HTTP 响应对象
     * @param asDownload 是否作为下载（true: 下载模式, false: 预览模式）
     */
    @Override
    public void streamFile(String objectName, HttpServletResponse response, boolean asDownload) {
        // 先从数据库获取文件信息
        File fileEntity = fileMapper.selectOne(
                new LambdaQueryWrapper<File>().eq(File::getFileName, objectName));

        if (fileEntity == null) {
            throw new BusinessException("文件不存在");
        }

        streamFromBucket(fileEntity.getBucketType(), objectName, response, asDownload, fileEntity);
    }

    /**
     * 流式输出文件
     * <p>
     * 支持预览和下载两种模式
     *
     * @param bucketType 桶类型（avatar/file）
     * @param objectName 文件对象名称（路径）
     * @param response   HTTP 响应对象
     * @param asDownload 是否作为下载（true: 下载模式, false: 预览模式）
     */
    @Override
    public void streamFromBucket(String bucketType, String objectName, HttpServletResponse response, boolean asDownload) {
        streamFromBucket(bucketType, objectName, response, asDownload, null);
    }


    /**
     * 流式输出文件（带数据库记录）
     *
     * @param bucketType 桶类型（avatar/file）
     * @param objectName 文件对象名称（路径）
     * @param response   HTTP 响应对象
     * @param asDownload 是否作为下载（true: 下载模式, false: 预览模式）
     * @param fileEntity 文件实体（可选，用于设置响应头）
     */
    private void streamFromBucket(String bucketType, String objectName, HttpServletResponse response, boolean asDownload, File fileEntity) {
        InputStream inputStream = null;
        try {
            String bucketName = minioProperties.getBucketName(bucketType);

            GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());

            inputStream = object;

            // 优先使用数据库记录的信息设置响应头
            String contentType;
            long size;

            if (fileEntity != null) {
                contentType = StrUtil.isNotBlank(fileEntity.getContentType())
                        ? fileEntity.getContentType() : "application/octet-stream";
                size = fileEntity.getFileSize() != null ? fileEntity.getFileSize() : -1L;
            } else {
                // 从 MinIO headers 获取
                contentType = object.headers().get("Content-Type");
                contentType = StrUtil.isNotBlank(contentType) ? contentType : "application/octet-stream";
                String contentLength = object.headers().get("Content-Length");
                size = StrUtil.isNotBlank(contentLength) ? Long.parseLong(contentLength) : -1L;
            }

            // 设置响应头
            response.setContentType(contentType);
            if (size > 0) {
                response.setContentLengthLong(size);
            }

            if (asDownload) {
                // 使用原始文件名
                String downloadName = fileEntity != null && StrUtil.isNotBlank(fileEntity.getOriginalFilename())
                        ? fileEntity.getOriginalFilename()
                        : objectName.substring(objectName.lastIndexOf('/') + 1);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\"");
            }

            // 流式输出
            IoUtil.copy(inputStream, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("获取文件失败: {}", e.getMessage());
            throw new BusinessException("获取文件失败: " + e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * 创建桶（如果不存在）
     *
     * @param bucketName 桶名称
     */
    private void createBucket(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("创建桶: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("创建桶失败 {}: {}", bucketName, e.getMessage());
        }
    }

    /**
     * 验证上传文件
     *
     * @param file     上传的文件
     * @param isAvatar 是否为头像上传
     */
    private void validateFile(MultipartFile file, boolean isAvatar) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 检查文件大小
        long maxSize = isAvatar ? MAX_AVATAR_SIZE : MAX_FILE_SIZE;
        if (file.getSize() > maxSize) {
            String limit = isAvatar ? "5MB" : "50MB";
            throw new BusinessException("文件大小超出限制，最大允许 " + limit);
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BusinessException("文件名不能为空");
        }

        String extension = FileUtil.extName(originalFilename);
        if (StrUtil.isBlank(extension)) {
            throw new BusinessException("无法识别文件类型");
        }

        Set<String> allowedExtensions = isAvatar ? ALLOWED_AVATAR_EXTENSIONS : ALLOWED_EXTENSIONS;
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("不允许上传该类型的文件");
        }
    }

    /**
     * 安全化扩展名
     * <p>
     * 只允许白名单中的扩展名，防止路径遍历攻击
     *
     * @param extension 原始扩展名
     * @return 安全的扩展名
     */
    private String sanitizeExtension(String extension) {
        if (StrUtil.isBlank(extension)) {
            return "bin";
        }
        // 移除特殊字符，只保留字母数字
        String safe = extension.toLowerCase().replaceAll("[^a-z0-9]", "");
        // 如果不在白名单中，使用默认值
        if (!ALLOWED_EXTENSIONS.contains(safe)) {
            return "bin";
        }
        return safe;
    }

    /**
     * 实体转 VO
     *
     * @param file 文件实体
     * @return 文件 VO
     */
    private FileVO convertToVO(File file) {
        FileVO vo = new FileVO();
        vo.setId(file.getId());
        vo.setBucket(file.getBucketType());
        vo.setFileName(file.getFileName());
        vo.setOriginalFilename(file.getOriginalFilename());
        vo.setSize(file.getFileSize());
        vo.setContentType(file.getContentType());
        vo.setUploadTime(file.getCreateTime());
        return vo;
    }
}