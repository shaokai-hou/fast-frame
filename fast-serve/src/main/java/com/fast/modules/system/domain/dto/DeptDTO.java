package com.fast.modules.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 部门DTO
 *
 * @author fast-frame
 */
@Data
public class DeptDTO implements Serializable {

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
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String deptName;

    /**
     * 部门标识
     */
    @Size(max = 50, message = "部门标识长度不能超过50个字符")
    private String deptKey;

    /**
     * 负责人
     */
    @Size(max = 50, message = "负责人长度不能超过50个字符")
    private String leader;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 状态(0正常 1禁用)
     */
    private String status;
}