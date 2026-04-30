package com.fast.common.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author haohao
 */
public class FileConstants {

    /**
     * 允许的文件扩展名白名单
     */
    public static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
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
     * 允许的图片扩展名（头像上传）
     */
    public static final Set<String> ALLOWED_AVATAR_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    /**
     * 最大文件大小（字节）：50MB
     */
    public static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    /**
     * 头像最大文件大小（字节）：5MB
     */
    public static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024;

}
