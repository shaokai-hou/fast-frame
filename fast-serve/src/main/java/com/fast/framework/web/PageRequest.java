package com.fast.framework.web;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页请求参数
 *
 * @author fast-frame
 */
@Data
public class PageRequest {

    /**
     * 页码（从 1 开始）
     */
    @Min(1)
    private Long pageNum = 1L;

    /**
     * 每页数量
     */
    @Min(1)
    @Max(100)
    private Long pageSize = 10L;
}