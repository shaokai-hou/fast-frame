import { hasPermission } from '@/utils/permission'

/**
 * 权限指令
 * 用法: v-hasPermi="['system:user:add']" 或 v-hasPermi="'system:user:add'"
 * 无权限时移除元素
 */
export const hasPermi = {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  updated(el, binding) {
    checkPermission(el, binding)
  }
}

/**
 * 检查权限
 */
function checkPermission(el, binding) {
  const value = binding.value

  if (!value) {
    return
  }

  let hasAuth = false

  if (Array.isArray(value)) {
    // 数组形式：拥有任一权限即显示
    hasAuth = value.some(p => hasPermission(p))
  } else if (typeof value === 'string') {
    // 字符串形式
    hasAuth = hasPermission(value)
  }

  if (!hasAuth) {
    el.parentNode?.removeChild(el)
  }
}

/**
 * 注册所有自定义指令
 */
export function setupDirectives(app) {
  app.directive('hasPermi', hasPermi)
}