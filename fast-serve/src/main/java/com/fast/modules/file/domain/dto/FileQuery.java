package com.fast.modules.file.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件查询DTO
 *
 * @author fast-frame
 */
@Data
public class FileQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件名（模糊查询）
     */
    private String fileName;

    /**
     * 桶类型
     */
    private String bucketType;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}