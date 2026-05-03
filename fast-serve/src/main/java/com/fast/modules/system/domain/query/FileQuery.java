package com.fast.modules.system.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 文件查询DTO
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileQuery extends PageRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 文件名（模糊查询）
     */
    private String fileName;

    /**
     * 桶类型
     */
    private String bucketType;
}