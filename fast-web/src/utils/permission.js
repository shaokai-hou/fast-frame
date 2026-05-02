import { usePermissionStore } from '@/store/permission'

const SUPER_PERMISSION = '*'

/**
 * 检查是否有权限
 */
export function hasPermission(permission) {
  const permissionStore = usePermissionStore()
  const permissions = permissionStore.permissions

  // 有上帝权限则直接返回 true
  if (permissions.includes(SUPER_PERMISSION)) {
    return true
  }

  return permissions.includes(permission)
}

/**
 * 检查是否有任一权限
 */
export function hasAnyPermission(permissions) {
  const permissionStore = usePermissionStore()
  const perms = permissionStore.permissions

  // 有上帝权限则直接返回 true
  if (perms.includes(SUPER_PERMISSION)) {
    return true
  }

  return permissions.some(p => perms.includes(p))
}

/**
 * 检查是否有所有权限
 */
export function hasAllPermission(permissions) {
  const permissionStore = usePermissionStore()
  const perms = permissionStore.permissions

  // 有上帝权限则直接返回 true
  if (perms.includes(SUPER_PERMISSION)) {
    return true
  }

  return permissions.every(p => perms.includes(p))
}