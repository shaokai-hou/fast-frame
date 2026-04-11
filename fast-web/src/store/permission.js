import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePermissionStore = defineStore('permission', () => {
  // 权限列表
  const permissions = ref([])
  // 动态路由
  const routes = ref([])
  // 是否已加载路由
  const routesLoaded = ref(false)

  // 设置权限
  function setPermissions(perms) {
    permissions.value = perms
  }

  // 设置路由
  function setRoutes(rts) {
    routes.value = rts
    routesLoaded.value = true
  }

  // 重置
  function reset() {
    permissions.value = []
    routes.value = []
    routesLoaded.value = false
  }

  return {
    permissions,
    routes,
    routesLoaded,
    setPermissions,
    setRoutes,
    reset
  }
})