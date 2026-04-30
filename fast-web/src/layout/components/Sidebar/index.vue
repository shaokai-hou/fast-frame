<template>
  <el-scrollbar class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      :collapse="appStore.sidebarCollapsed"
      :unique-opened="true"
      background-color="transparent"
      text-color="var(--color-foreground-muted)"
      active-text-color="var(--color-primary)"
    >
      <!-- 首页菜单（静态） -->
      <el-menu-item index="/home" @click="handleMenuClick('/home')">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>

      <!-- 动态路由菜单 -->
      <SidebarItem
        v-for="route in routes"
        :key="route.path"
        :item="route"
      />
    </el-menu>
  </el-scrollbar>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { usePermissionStore } from '@/store/permission'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const permissionStore = usePermissionStore()

const activeMenu = computed(() => {
  return route.path
})

const routes = computed(() => {
  return permissionStore.routes
})

/**
 * 判断是否为外部链接
 */
function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * 处理菜单点击（手动路由跳转）
 */
function handleMenuClick(path) {
  if (isExternal(path)) {
    // 外链：新窗口打开
    window.open(path, '_blank', 'noopener')
  } else {
    // 内链：路由跳转
    router.push(path)
  }
}
</script>

<style scoped lang="scss">
.sidebar-container {
  height: calc(100vh - 60px);
}

.el-menu {
  border-right: none;
  padding: 8px 0;

  // 菜单项基础样式
  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    transition: all 0.2s ease;
    margin: 3px 10px;
    border-radius: 10px;
    height: 42px;
    line-height: 42px;
    position: relative;
    padding-left: 16px !important;

    &:hover {
      background-color: var(--gray-100) !important;
      color: var(--color-foreground);
      transform: translateX(2px);
    }
  }

  // 激活菜单项 - 渐变背景 + 左侧指示条
  :deep(.el-menu-item.is-active) {
    background: linear-gradient(90deg, var(--color-primary-lighter) 0%, transparent 100%) !important;
    color: var(--color-primary) !important;
    font-weight: 600;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 8px;
      bottom: 8px;
      width: 4px;
      background: var(--color-primary);
      border-radius: 0 4px 4px 0;
      box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
    }

    .el-icon {
      color: var(--color-primary) !important;
    }
  }

  // 子菜单父级激活
  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: var(--color-primary);
    font-weight: 500;

    .el-icon:first-child {
      color: var(--color-primary);
    }
  }

  // 子菜单项
  :deep(.el-sub-menu .el-menu) {
    background: transparent !important;
  }

  :deep(.el-sub-menu .el-menu-item) {
    min-width: auto;
    padding-left: 48px !important;
    margin: 2px 10px;
    height: 38px;
    line-height: 38px;
    font-size: 13px;
    border-radius: 8px;

    &:hover {
      transform: none;
    }

    // 子菜单激活项指示条位置
    &.is-active::before {
      left: 34px;
      top: 10px;
      bottom: 10px;
      width: 3px;
    }
  }

  // 子菜单展开时标题样式
  :deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
    color: var(--color-foreground);
    font-weight: 500;
  }

  // 展开图标
  :deep(.el-sub-menu__icon-arrow) {
    font-size: 12px;
    color: var(--color-foreground-muted);
    transition: transform 0.3s ease;
  }

  :deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
    transform: rotate(180deg);
    color: var(--color-foreground);
  }

  // 菜单图标
  :deep(.el-icon:first-child) {
    font-size: 18px;
    margin-right: 12px;
    color: var(--color-foreground-muted);
    transition: color 0.2s ease;
  }

  :deep(.el-menu-item:hover .el-icon:first-child),
  :deep(.el-sub-menu__title:hover .el-icon:first-child) {
    color: var(--color-foreground);
  }

  :deep(.el-menu-item.is-active .el-icon:first-child) {
    color: var(--color-primary);
  }

  // ==========================================
  // 折叠状态样式
  // ==========================================
  &.el-menu--collapse {
    // 菜单项和子菜单标题通用样式
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: 52px !important;
      line-height: 52px !important;
      margin: 4px 6px;
      padding: 0 !important;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      width: 52px;
      border-radius: 12px;
      position: relative;

      // 覆盖 Element Plus 的 vertical-align: bottom
      * {
        vertical-align: middle !important;
      }

      .el-icon {
        margin: 0 !important;
        font-size: 20px !important;
        width: 24px;
        text-align: center;
      }

      span {
        display: none !important;
      }

      .el-sub-menu__icon-arrow {
        display: none !important;
      }

      &:hover {
        background-color: var(--gray-100) !important;
        color: var(--color-foreground) !important;

        .el-icon {
          color: var(--color-foreground) !important;
        }
      }
    }

    // 子菜单容器居中
    :deep(.el-sub-menu) {
      display: flex;
      justify-content: center;
    }

    // Tooltip 触发器
    :deep(.el-menu-tooltip__trigger) {
      width: 100% !important;
      height: 100% !important;
      padding: 0 !important;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      position: absolute !important;
      top: 0 !important;
      left: 0 !important;
    }

    // 激活项样式
    :deep(.el-menu-item.is-active),
    :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
      background: var(--color-primary-lighter) !important;
      color: var(--color-primary) !important;
      font-weight: 600;

      .el-icon {
        color: var(--color-primary) !important;
      }
    }
  }
}
</style>