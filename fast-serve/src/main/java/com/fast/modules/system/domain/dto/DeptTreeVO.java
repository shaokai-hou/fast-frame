package com.fast.modules.system.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 部门树形选择器VO
 *
 * @author fast-frame
 */
@Data
public class DeptTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String label;

    /**
     * 子部门
     */
    private List<DeptTreeVO> children;
}