package com.fast.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @author fast-frame
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * 返回成功结果（无数据）
     *
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 返回成功结果（带数据）
     *
     * @param <T> 数据类型
     * @param data 返回数据
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 返回成功结果（带数据和消息）
     *
     * @param <T> 数据类型
     * @param data 返回数据
     * @param msg  返回消息
     * @return 成功结果
     */
    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 返回失败结果（默认消息）
     *
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> fail() {
        return fail("操作失败");
    }

    /**
     * 返回失败结果（带消息）
     *
     * @param <T> 数据类型
     * @param msg 失败消息
     * @return 失败结果
     */
    public static <T> Result<T> fail(String msg) {
        return fail(500, msg);
    }

    /**
     * 返回失败结果（带错误码和消息）
     *
     * @param <T> 数据类型
     * @param code 错误码
     * @param msg  失败消息
     * @return 失败结果
     */
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}