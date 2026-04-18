package com.fast.modules.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色查询参数
 *
 * @author fast-frame
 */
@Data
public class RoleQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 状态
     */
    private String status;
}