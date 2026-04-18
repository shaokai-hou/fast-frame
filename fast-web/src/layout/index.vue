<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div
      class="sidebar"
      :class="{ collapsed: appStore.sidebarCollapsed, 'is-collapsing': isCollapsing }"
    >
      <Logo v-if="settingsStore.showLogo" />
      <Sidebar />
    </div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <Navbar />

      <!-- TagsView 标签页导航 -->
      <TagsView v-if="settingsStore.showTagsView" />

      <!-- 内容区 -->
      <AppMain />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useAppStore } from '@/store/app'
import { useSettingsStore } from '@/store/settings'
import Logo from './components/Sidebar/Logo.vue'
import Sidebar from './components/Sidebar/index.vue'
import Navbar from './components/Navbar/index.vue'
import TagsView from './components/TagsView/index.vue'
import AppMain from './components/AppMain.vue'

const appStore = useAppStore()
const settingsStore = useSettingsStore()
const isCollapsing = ref(false)

// 监听收缩状态变化，在动画期间添加标记 class
watch(
  () => appStore.sidebarCollapsed,
  () => {
    isCollapsing.value = true
    // 动画结束后移除标记
    setTimeout(() => {
      isCollapsing.value = false
    }, 300) // 与 CSS transition 时间一致
  }
)

onMounted(() => {
  // 初始化设置
  settingsStore.initSettings()
})
</script>

<style scoped lang="scss">
.layout-container {
  display: flex;
  height: 100%;
  overflow: hidden;
  background: var(--color-background);
}

.sidebar {
  width: 240px;
  background: var(--color-surface);
  border-right: 1px solid var(--color-border);
  transition: width 0.28s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  display: flex;
  flex-direction: column;

  &.collapsed {
    width: 64px;
  }

  // 收缩动画期间，禁用内部元素的 transition 以避免卡顿
  &.is-collapsing {
    :deep(*) {
      transition: none !important;
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}
</style>