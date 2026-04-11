package com.fast.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
     * 性别(0未知 1男 2女)
     */
    private String gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态(0正常 1禁用)
     */
    private String status;

    /**
     * 删除标志(0正常 1删除)
     */
    @TableLogic
    private String delFlag;
}