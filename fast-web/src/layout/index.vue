<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div
      class="sidebar"
      :class="{ collapsed: appStore.sidebarCollapsed, 'is-collapsing': isCollapsing }"
    >
      <Logo />
      <Sidebar />
    </div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <Navbar />

      <!-- 内容区 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="slide-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useAppStore } from '@/store/app'
import Logo from './components/Sidebar/Logo.vue'
import Sidebar from './components/Sidebar/index.vue'
import Navbar from './components/Navbar/index.vue'

const appStore = useAppStore()
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
  appStore.initDarkMode()
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

.app-main {
  flex: 1;
  padding: 24px;
  overflow: auto;
  background: var(--color-background);
}

// 页面过渡动画
.slide-fade-enter-active {
  transition: all 0.25s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s ease-in;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>