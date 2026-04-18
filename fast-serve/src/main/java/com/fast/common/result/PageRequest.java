package com.fast.common.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    @Min(1)
    @Max(100)
    private Integer pageSize = 10;

    /**
     * 获取起始偏移量（用于 SQL LIMIT offset）
     *
     * @return 偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取 MyBatis Plus Page 对象
     *
     * @param <T> 数据类型
     * @return Page 对象
     */
    public <T> Page<T> toPage() {
        return new Page<>(pageNum, pageSize);
    }
}