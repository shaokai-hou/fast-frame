package com.fast.modules.file.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class File extends BaseEntity {

    /**
     * 对象名（yyyy/MM/dd/uuid.ext）
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 桶类型（avatar/file）
     */
    private String bucketType;

    /**
     * 文件类型（MIME）
     */
    private String contentType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;
}