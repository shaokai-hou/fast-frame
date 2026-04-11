package com.fast.framework.security;

import cn.dev33.satoken.stp.StpInterface;
import com.fast.modules.system.service.MenuService;
import com.fast.modules.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        return roleService.listRoleKeysByUserId(userId);
    }
}