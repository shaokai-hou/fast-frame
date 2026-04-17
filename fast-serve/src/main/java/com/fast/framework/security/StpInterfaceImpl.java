package com.fast.framework.security;

import cn.dev33.satoken.stp.StpInterface;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.service.MenuService;
import com.fast.modules.system.service.RoleService;
import com.fast.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限实现
 *
 * @author fast-frame
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final MenuService menuService;
    private final RoleService roleService;
    private final UserService userService;

    private static final String ADMIN_USERNAME = "admin";
    private static final String SUPER_PERMISSION = "*:*:*";
    private static final String ADMIN_ROLE = "admin";

    /**
     * 判断用户是否为超级管理员
     *
     * @param userId 用户ID
     * @return 是否为超级管理员
     */
    private boolean isAdmin(Long userId) {
        User user = userService.getById(userId);
        return user != null && ADMIN_USERNAME.equals(user.getUsername());
    }

    /**
     * 返回一个账号所拥有的权限码集合
     *
     * @param loginId   登录ID
     * @param loginType 登录类型
     * @return 权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        // admin 用户直接返回超级权限
        if (isAdmin(userId)) {
            return Collections.singletonList(SUPER_PERMISSION);
        }
        return menuService.listPermissionsByUserId(userId);
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     *
     * @param loginId   登录ID
     * @param loginType 登录类型
     * @return 角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        // admin 用户直接返回 admin 角色
        if (isAdmin(userId)) {
            return Collections.singletonList(ADMIN_ROLE);
        }
        return roleService.listRoleKeysByUserId(userId);
    }
}