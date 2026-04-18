package com.fast.modules.system.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询参数
 *
 * @author fast-frame
 */
@Data
public class UserQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     */
    private String gender;

    /**
     * 状态
     */
    private String status;
/**
     * 数据权限SQL（由切面注入）
     */
    private String dataScope;
}