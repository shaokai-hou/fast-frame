import { usePermissionStore } from '@/store/permission'

/**
 * 检查是否有权限
 */
export function hasPermission(permission) {
  const permissionStore = usePermissionStore()
  return permissionStore.permissions.includes(permission)
}

/**
 * 检查是否有任一权限
 */
export function hasAnyPermission(permissions) {
  const permissionStore = usePermissionStore()
  return permissions.some(p => permissionStore.permissions.includes(p))
}

/**
 * 检查是否有所有权限
 */
export function hasAllPermission(permissions) {
  const permissionStore = usePermissionStore()
  return permissions.every(p => permissionStore.permissions.includes(p))
}