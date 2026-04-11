package com.fast.framework.web;

import com.fast.common.result.PageResult;
import com.fast.common.result.Result;

import java.util.List;

/**
 * 基础控制器
 *
 * @author fast-frame
 */
public class BaseController {

    /**
     * 返回成功
     *
     * @param <T> 数据类型
     * @return 成功结果
     */
    protected <T> Result<T> success() {
        return Result.success();
    }

    /**
     * 返回成功
     *
     * @param <T> 数据类型
     * @param data 返回数据
     * @return 成功结果
     */
    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    /**
     * 返回成功
     *
     * @param <T> 数据类型
     * @param data 返回数据
     * @param msg  返回消息
     * @return 成功结果
     */
    protected <T> Result<T> success(T data, String msg) {
        return Result.success(data, msg);
    }

    /**
     * 返回失败
     *
     * @param <T> 数据类型
     * @return 失败结果
     */
    protected <T> Result<T> fail() {
        return Result.fail();
    }

    /**
     * 返回失败
     *
     * @param <T> 数据类型
     * @param msg 失败消息
     * @return 失败结果
     */
    protected <T> Result<T> fail(String msg) {
        return Result.fail(msg);
    }

    /**
     * 返回失败
     *
     * @param <T> 数据类型
     * @param code 错误码
     * @param msg  失败消息
     * @return 失败结果
     */
    protected <T> Result<T> fail(int code, String msg) {
        return Result.fail(code, msg);
    }

    /**
     * 返回分页数据
     *
     * @param <T>   数据类型
     * @param list  数据列表
     * @param total 总数
     * @return 分页结果
     */
    protected <T> Result<PageResult<T>> pageResult(List<T> list, long total) {
        return success(PageResult.of(list, total));
    }
}