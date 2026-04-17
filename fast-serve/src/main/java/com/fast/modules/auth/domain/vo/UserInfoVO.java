package com.fast.modules.auth.domain.vo;

import com.fast.modules.system.domain.vo.RoleVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息VO
 *
 * @author fast-frame
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 角色列表
     */
    private List<RoleVO> roles;

    /**
     * 权限列表
     */
    private List<String> permissions;
}