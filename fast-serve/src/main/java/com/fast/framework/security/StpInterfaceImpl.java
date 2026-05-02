package com.fast.framework.security;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.TypeReference;
import com.fast.framework.helper.AdminHelper;
import com.fast.framework.helper.RedisHelper;
import com.fast.modules.system.service.MenuService;
import com.fast.modules.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限实现
 * <p>
 * 权限说明：
 * - admin 用户返回 "*" 上帝权限，可匹配所有权限
 * - 普通用户通过角色-菜单关联获取对应权限
 * - 权限数据使用 Redis 缓存，减少数据库访问
 *
 * @author fast-frame
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final MenuService menuService;
    private final RoleService roleService;

    private static final String CACHE_KEY_PERMISSION = "sa-token:permission:";
    private static final String CACHE_KEY_ROLE = "sa-token:role:";
    private static final long CACHE_EXPIRE = 60 * 60 * 24 * 30;


    /**
     * 返回一个账号所拥有的权限码集合
     * admin 用户返回 "*" 上帝权限，可匹配所有权限
     * 普通用户从缓存或数据库查询权限
     *
     * @param loginId   登录ID
     * @param loginType 登录类型
     * @return 权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        if (AdminHelper.isAdmin(userId)) {
            return Collections.singletonList(AdminHelper.getSuperPermission());
        }
        String cacheKey = CACHE_KEY_PERMISSION + userId;
        List<String> permissionList = RedisHelper.getJson(cacheKey, new TypeReference<List<String>>() {
        });
        if (CollUtil.isEmpty(permissionList)) {
            permissionList = menuService.listPermissionsByUserId(userId);
            RedisHelper.setJson(cacheKey, permissionList, CACHE_EXPIRE);
        }
        return permissionList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     * admin 用户返回 admin 角色
     * 普通用户从缓存或数据库查询角色
     *
     * @param loginId   登录ID
     * @param loginType 登录类型
     * @return 角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        if (AdminHelper.isAdmin(userId)) {
            return Collections.singletonList(AdminHelper.getAdminRoleKey());
        }
        String cacheKey = CACHE_KEY_ROLE + userId;
        List<String> roleList = RedisHelper.getJson(cacheKey, new TypeReference<List<String>>() {
        });
        if (CollUtil.isEmpty(roleList)) {
            roleList = roleService.listRoleKeysByUserId(userId);
            RedisHelper.setJson(cacheKey, roleList, CACHE_EXPIRE);
        }
        return roleList;
    }

}