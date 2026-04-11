package com.fast.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 *
 * @author fast-frame
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总数
     */
    private long total;

    /**
     * 默认构造方法
     */
    public PageResult() {
    }

    /**
     * 带参数构造方法
     *
     * @param records 数据列表
     * @param total   总数
     */
    public PageResult(List<T> records, long total) {
        this.records = records;
        this.total = total;
    }

    /**
     * 创建分页结果
     *
     * @param <T>    数据类型
     * @param records 数据列表
     * @param total   总数
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, long total) {
        return new PageResult<>(records, total);
    }
}