<template>
  <div class="navbar">
    <div class="left-menu">
      <!-- 折叠按钮 -->
      <div
        class="action-btn"
        @click="toggleSidebar"
      >
        <el-icon :size="20">
          <Fold v-if="!appStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </div>

      <!-- 面包屑 -->
      <Breadcrumb />
    </div>

    <div class="right-menu">
      <!-- 消息提醒 -->
      <MessageBell />

      <!-- 全屏切换 -->
      <Screenfull />

      <!-- 设置按钮 -->
      <div
        class="action-btn"
        @click="openSettings"
      >
        <el-icon :size="18">
          <Setting />
        </el-icon>
      </div>

      <!-- 用户下拉菜单 -->
      <UserDropdown />
    </div>

    <!-- 设置抽屉 -->
    <Settings ref="settingsRef" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAppStore } from '@/store/app'
import { Fold, Expand, Setting } from '@element-plus/icons-vue'
import Breadcrumb from './Breadcrumb.vue'
import Screenfull from './Screenfull.vue'
import UserDropdown from './UserDropdown.vue'
import Settings from '../Settings/index.vue'
import MessageBell from '@/components/MessageBell/index.vue'

const appStore = useAppStore()
const settingsRef = ref(null)

function toggleSidebar() {
  appStore.toggleSidebar()
}

function openSettings() {
  settingsRef.value?.openSettings()
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
  box-shadow: 0 2px 8px rgba(0, 21, 41, 0.1);

  .action-btn {
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

.left-menu {
  display: flex;
  align-items: center;
  gap: 16px;
}

.right-menu {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>