import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

const STORAGE_KEY = 'fast-frame-settings'

// 默认配置
const defaultSettings = {
  showTagsView: true,
  showLogo: true,
  fixedHeader: true
}

export const useSettingsStore = defineStore('settings', () => {
  // 显示 TagsView
  const showTagsView = ref(defaultSettings.showTagsView)
  // 显示 Logo
  const showLogo = ref(defaultSettings.showLogo)
  // 固定头部
  const fixedHeader = ref(defaultSettings.fixedHeader)

  // 从 localStorage 加载配置
  function initSettings() {
    try {
      const saved = localStorage.getItem(STORAGE_KEY)
      if (saved) {
        const parsed = JSON.parse(saved)
        showTagsView.value = parsed.showTagsView !== false
        showLogo.value = parsed.showLogo !== false
        fixedHeader.value = parsed.fixedHeader !== false
      }
    } catch (e) {
      console.warn('Failed to load settings from localStorage:', e)
    }
  }

  // 保存配置到 localStorage
  function saveSettings() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify({
        showTagsView: showTagsView.value,
        showLogo: showLogo.value,
        fixedHeader: fixedHeader.value
      }))
    } catch (e) {
      console.warn('Failed to save settings to localStorage:', e)
    }
  }

  // 切换 TagsView 显示
  function toggleTagsView() {
    showTagsView.value = !showTagsView.value
    saveSettings()
  }

  // 切换 Logo 显示
  function toggleLogo() {
    showLogo.value = !showLogo.value
    saveSettings()
  }

  return {
    showTagsView,
    showLogo,
    fixedHeader,
    initSettings,
    saveSettings,
    toggleTagsView,
    toggleLogo
  }
})