import { defineStore } from 'pinia'
import { ref } from 'vue'

// 首页路径（不可关闭）
const HOME_PATH = '/home'

export const useTagsViewStore = defineStore('tagsView', () => {
  // 已访问的路由列表
  const visitedRoutes = ref([])
  // 缓存的路由组件名称列表（用于 keep-alive）
  const cachedRoutes = ref([])

  // 初始化，添加首页
  function initTagsView() {
    // 首页默认添加
    addVisitedRoute({
      path: HOME_PATH,
      name: 'Home',
      meta: { title: '首页', icon: 'HomeFilled' },
      title: '首页'
    })
    addCachedRoute('Home')
  }

  // 添加已访问路由
  function addVisitedRoute(route) {
    // 检查是否已存在
    const exists = visitedRoutes.value.some(r => r.path === route.path)
    if (!exists) {
      visitedRoutes.value.push({
        path: route.path,
        name: route.name,
        meta: { ...route.meta },
        fullPath: route.fullPath || route.path,
        title: route.meta?.title || 'no-name'
      })
    } else {
      // 更新已存在的路由信息
      const index = visitedRoutes.value.findIndex(r => r.path === route.path)
      if (index > -1) {
        visitedRoutes.value[index].fullPath = route.fullPath || route.path
      }
    }
  }

  // 移除已访问路由（首页不可移除）
  function removeVisitedRoute(route) {
    // 首页不可移除
    if (route.path === HOME_PATH) return false

    const index = visitedRoutes.value.findIndex(r => r.path === route.path)
    if (index > -1) {
      visitedRoutes.value.splice(index, 1)
    }
    // 同时移除缓存
    if (route.name) {
      removeCachedRoute(route.name)
    }
    return true
  }

  // 关闭其他路由（保留当前和首页）
  function closeOtherRoutes(route) {
    visitedRoutes.value = visitedRoutes.value.filter(r => r.path === route.path || r.path === HOME_PATH)
    // 缓存只保留当前和首页
    cachedRoutes.value = cachedRoutes.value.filter(name => name === route.name || name === 'Home')
  }

  // 关闭左侧路由（保留首页）
  function closeLeftRoutes(route) {
    const index = visitedRoutes.value.findIndex(r => r.path === route.path)
    if (index > -1) {
      const leftRoutes = visitedRoutes.value.slice(0, index)
      visitedRoutes.value = visitedRoutes.value.filter(r => r.path === HOME_PATH || !leftRoutes.some(lr => lr.path === r.path))
      // 移除左侧缓存（首页除外）
      leftRoutes.forEach(lr => {
        if (lr.name && lr.path !== HOME_PATH) {
          removeCachedRoute(lr.name)
        }
      })
    }
  }

  // 关闭右侧路由（保留首页）
  function closeRightRoutes(route) {
    const index = visitedRoutes.value.findIndex(r => r.path === route.path)
    if (index > -1) {
      const rightRoutes = visitedRoutes.value.slice(index + 1)
      visitedRoutes.value = visitedRoutes.value.filter(r => r.path === HOME_PATH || !rightRoutes.some(rr => rr.path === r.path))
      // 移除右侧缓存（首页除外）
      rightRoutes.forEach(rr => {
        if (rr.name && rr.path !== HOME_PATH) {
          removeCachedRoute(rr.name)
        }
      })
    }
  }

  // 关闭所有路由（只保留首页）
  function closeAllRoutes() {
    visitedRoutes.value = visitedRoutes.value.filter(r => r.path === HOME_PATH)
    cachedRoutes.value = cachedRoutes.value.filter(name => name === 'Home')
  }

  // 更新已访问路由标题
  function updateVisitedRoute(route) {
    const index = visitedRoutes.value.findIndex(r => r.path === route.path)
    if (index > -1) {
      visitedRoutes.value[index].title = route.meta?.title || 'no-name'
    }
  }

  // 添加缓存路由
  function addCachedRoute(name) {
    if (name && !cachedRoutes.value.includes(name)) {
      cachedRoutes.value.push(name)
    }
  }

  // 移除缓存路由
  function removeCachedRoute(name) {
    // 首页缓存不移除
    if (name === 'Home') return

    const index = cachedRoutes.value.indexOf(name)
    if (index > -1) {
      cachedRoutes.value.splice(index, 1)
    }
  }

  // 刷新当前路由（移除缓存后重新添加）
  function refreshRoute(route) {
    if (route.name) {
      removeCachedRoute(route.name)
      // 下次进入时会重新缓存
    }
  }

  return {
    visitedRoutes,
    cachedRoutes,
    initTagsView,
    addVisitedRoute,
    removeVisitedRoute,
    closeOtherRoutes,
    closeLeftRoutes,
    closeRightRoutes,
    closeAllRoutes,
    updateVisitedRoute,
    addCachedRoute,
    removeCachedRoute,
    refreshRoute
  }
})