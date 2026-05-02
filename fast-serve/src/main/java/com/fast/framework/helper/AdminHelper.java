package com.fast.framework.helper;

/**
 * Admin 用户判断工具类
 * <p>
 * 统一管理 admin 用户判断逻辑，避免各处重复实现
 *
 * @author fast-frame
 */
public final class AdminHelper {

    private static final Long ADMIN_ID = 1L;
    private static final Long ADMIN_ROLE_ID = 1L;
    private static final String ADMIN_ROLE_KEY = "admin";
    private static final String SUPER_PERMISSION = "*";

    /**
     * 判断用户是否为超级管理员
     *
     * @param userId 用户ID
     * @return 是否为超级管理员
     */
    public static boolean isAdmin(Long userId) {
        return ADMIN_ID.equals(userId);
    }

    /**
     * 判断角色是否为管理员角色
     *
     * @param roleId 角色ID
     * @return 是否为管理员角色
     */
    public static boolean isAdminRole(Long roleId) {
        return ADMIN_ROLE_ID.equals(roleId);
    }

    /**
     * 获取管理员用户ID
     *
     * @return 管理员用户ID
     */
    public static Long getAdminId() {
        return ADMIN_ID;
    }

    /**
     * 获取管理员角色标识
     *
     * @return 管理员角色标识
     */
    public static String getAdminRoleKey() {
        return ADMIN_ROLE_KEY;
    }

    /**
     * 获取超级权限标识
     *
     * @return 超级权限标识
     */
    public static String getSuperPermission() {
        return SUPER_PERMISSION;
    }
}