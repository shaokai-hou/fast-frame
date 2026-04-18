package com.fast.modules.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 角色DTO
 *
 * @author fast-frame
 */
@Data
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @NotBlank(message = "角色权限字符串不能为空")
    @Size(max = 30, message = "角色权限字符串长度不能超过30个字符")
    private String roleKey;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)
     */
    private String dataScope;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;

    /**
     * 菜单ID列表
     */
    private Long[] menuIds;

    /**
     * 部门ID列表(自定义数据范围时使用)
     */
    private Long[] deptIds;
}