import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏是否折叠
  const sidebarCollapsed = ref(false)
  // 暗黑模式
  const isDark = ref(false)

  // 切换侧边栏
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 切换暗黑模式
  function toggleDark() {
    isDark.value = !isDark.value
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
    localStorage.setItem('dark-mode', isDark.value)
  }

  // 初始化暗黑模式
  function initDarkMode() {
    const saved = localStorage.getItem('dark-mode')
    if (saved === 'true') {
      isDark.value = true
      document.documentElement.classList.add('dark')
    }
  }

  return {
    sidebarCollapsed,
    isDark,
    toggleSidebar,
    toggleDark,
    initDarkMode
  }
})