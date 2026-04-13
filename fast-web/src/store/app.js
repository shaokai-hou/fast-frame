import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏是否折叠
  const sidebarCollapsed = ref(false)
  // 缓存的页面组件名称列表
  const cachedPages = ref([])
  // iframe页面视图列表
  const iframeViews = ref([])

  // 切换侧边栏
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 添加缓存页面
  function addCachedPage(name) {
    if (name && !cachedPages.value.includes(name)) {
      cachedPages.value.push(name)
    }
  }

  // 移除缓存页面
  function removeCachedPage(name) {
    const index = cachedPages.value.indexOf(name)
    if (index > -1) {
      cachedPages.value.splice(index, 1)
    }
  }

  // 清除所有缓存页面
  function clearCachedPages() {
    cachedPages.value = []
  }

  // 添加 iframe 视图
  function addIframeView(route) {
    if (iframeViews.value.some(v => v.path === route.path)) return
    iframeViews.value.push({
      path: route.path,
      meta: route.meta
    })
  }

  // 删除 iframe 视图
  function delIframeView(path) {
    iframeViews.value = iframeViews.value.filter(v => v.path !== path)
  }

  return {
    sidebarCollapsed,
    cachedPages,
    iframeViews,
    toggleSidebar,
    addCachedPage,
    removeCachedPage,
    clearCachedPages,
    addIframeView,
    delIframeView
  }
})