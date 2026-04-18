package com.fast.modules.system.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件VO
 *
 * @author fast-frame
 */
@Data
public class FileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 桶类型（avatar/file）
     */
    private String bucket;

    /**
     * 文件名（对象名，yyyy/MM/dd/uuid.ext）
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 文件大小(字节)
     */
    private Long size;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
}