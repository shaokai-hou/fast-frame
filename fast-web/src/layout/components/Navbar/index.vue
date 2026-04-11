<template>
  <div class="navbar">
    <div class="left-menu">
      <!-- 折叠按钮 -->
      <div class="hamburger" @click="toggleSidebar">
        <el-icon :size="20">
          <Fold v-if="!appStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </div>

      <!-- 面包屑 -->
      <Breadcrumb />
    </div>

    <div class="right-menu">
      <!-- 暗黑模式切换 -->
      <ThemeSwitch />

      <!-- 全屏切换 -->
      <Screenfull />

      <!-- 用户下拉菜单 -->
      <UserDropdown />
    </div>
  </div>
</template>

<script setup>
import { useAppStore } from '@/store/app'
import { Fold, Expand } from '@element-plus/icons-vue'
import Breadcrumb from './Breadcrumb.vue'
import ThemeSwitch from './ThemeSwitch.vue'
import Screenfull from './Screenfull.vue'
import UserDropdown from './UserDropdown.vue'

const appStore = useAppStore()

function toggleSidebar() {
  appStore.toggleSidebar()
}
</script>

<style scoped lang="scss">
.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: var(--color-surface);
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
  border-bottom: 1px solid var(--color-border);
}

.left-menu {
  display: flex;
  align-items: center;
  gap: 16px;

  .hamburger {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    cursor: pointer;
    color: var(--color-foreground-muted);
    transition: all 0.2s ease;
    border-radius: 10px;

    &:hover {
      color: var(--color-primary);
      background: var(--color-primary-lighter);
    }
  }
}

.right-menu {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>