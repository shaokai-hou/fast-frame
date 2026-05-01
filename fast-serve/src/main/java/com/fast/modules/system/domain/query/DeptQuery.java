package com.fast.modules.system.domain.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 部门查询参数
 *
 * @author fast-frame
 */
@Data
public class DeptQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 状态(0正常 1禁用)
     */
    private String status;
}