<template>
  <div class="tags-view-container">
    <ScrollPane ref="scrollPaneRef" class="tags-scroll">
      <AppLink
        v-for="tag in tagsViewStore.visitedRoutes"
        :key="tag.path"
        :to="tag.path"
        :data-path="tag.path"
        class="tags-item"
        :class="{ active: isActive(tag) }"
        @contextmenu.prevent="openContextMenu(tag, $event)"
      >
        <span class="tag-title">{{ tag.title }}</span>
        <!-- 关闭按钮（首页不可关闭） -->
        <el-icon
          v-if="tag.path !== '/home'"
          class="close-icon"
          @click.prevent.stop="closeTag(tag)"
        >
          <Close />
        </el-icon>
      </AppLink>
    </ScrollPane>

    <!-- 右键菜单 -->
    <div
      v-show="contextMenuVisible"
      class="context-menu"
      :style="{ left: contextMenuLeft + 'px', top: contextMenuTop + 'px' }"
    >
      <div class="menu-item" @click="refreshSelectedTag">
        <el-icon><Refresh /></el-icon>
        <span>刷新页面</span>
      </div>
      <div class="menu-item" v-if="selectedTag?.path !== '/home'" @click="closeSelectedTag">
        <el-icon><Close /></el-icon>
        <span>关闭当前</span>
      </div>
      <div class="menu-item" @click="closeOtherTags">
        <el-icon><FolderRemove /></el-icon>
        <span>关闭其他</span>
      </div>
      <div class="menu-item" @click="closeLeftTags">
        <el-icon><Back /></el-icon>
        <span>关闭左侧</span>
      </div>
      <div class="menu-item" @click="closeRightTags">
        <el-icon><Right /></el-icon>
        <span>关闭右侧</span>
      </div>
      <div class="menu-item" @click="closeAllTags">
        <el-icon><CircleClose /></el-icon>
        <span>关闭全部</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useTagsViewStore } from '@/store/tagsView'
import { useAppStore } from '@/store/app'
import ScrollPane from './ScrollPane.vue'
import AppLink from '@/layout/components/Sidebar/Link.vue'
import {
  Close,
  Refresh,
  FolderRemove,
  Back,
  Right,
  CircleClose
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const tagsViewStore = useTagsViewStore()
const appStore = useAppStore()
const scrollPaneRef = ref(null)

// 右键菜单状态
const contextMenuVisible = ref(false)
const contextMenuLeft = ref(0)
const contextMenuTop = ref(0)
const selectedTag = ref(null)

// 判断是否为外部链接
function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

// 判断标签是否激活
const isActive = (tag) => {
  return route.path === tag.path
}

// 在 watch 之前初始化，确保 localStorage 数据已恢复
tagsViewStore.initTagsView()

// 监听路由变化，添加标签
watch(
  () => route.path,
  (path) => {
    // 不添加登录页、404 页和外链
    if (path === '/login' || path === '/404') return
    if (isExternal(path)) return

    // 添加已访问路由
    tagsViewStore.addVisitedRoute(route)

    // 添加缓存（如果没有 noCache）
    if (route.name && !route.meta?.noCache) {
      tagsViewStore.addCachedRoute(route.name)
    }

    // 同步到 app store 的 cachedPages
    appStore.cachedPages = [...tagsViewStore.cachedRoutes]

    // 滚动到当前标签
    nextTick(() => {
      scrollPaneRef.value?.scrollToTag(path)
    })
  },
  { immediate: true }
)

// 关闭单个标签
const closeTag = (tag) => {
  tagsViewStore.removeVisitedRoute(tag)
  // 同步缓存
  appStore.cachedPages = [...tagsViewStore.cachedRoutes]

  // 如果关闭的是当前页面，跳转到最后一个访问的页面或首页
  if (isActive(tag)) {
    const lastRoute = tagsViewStore.visitedRoutes[tagsViewStore.visitedRoutes.length - 1]
    if (lastRoute) {
      router.push(lastRoute.path)
    } else {
      router.push('/home')
    }
  }
}

// 打开右键菜单
const openContextMenu = (tag, e) => {
  selectedTag.value = tag
  contextMenuLeft.value = e.clientX
  contextMenuTop.value = e.clientY

  // 计算菜单位置，避免超出屏幕
  const menuWidth = 120
  const menuHeight = 200
  const viewportWidth = window.innerWidth
  const viewportHeight = window.innerHeight

  if (contextMenuLeft.value + menuWidth > viewportWidth) {
    contextMenuLeft.value = viewportWidth - menuWidth - 10
  }
  if (contextMenuTop.value + menuHeight > viewportHeight) {
    contextMenuTop.value = viewportHeight - menuHeight - 10
  }

  contextMenuVisible.value = true
}

// 关闭右键菜单（点击其他区域）
const closeContextMenu = () => {
  contextMenuVisible.value = false
}

// 监听全局点击关闭菜单
watch(contextMenuVisible, (visible) => {
  if (visible) {
    document.addEventListener('click', closeContextMenu)
  } else {
    document.removeEventListener('click', closeContextMenu)
  }
})

// 刷新当前标签
const refreshSelectedTag = () => {
  tagsViewStore.refreshRoute(selectedTag.value)
  // 移除缓存
  appStore.cachedPages = [...tagsViewStore.cachedRoutes]

  // 重新加载页面（通过路由重定向）
  router.replace({ path: '/redirect' + selectedTag.value.path })
}

// 关闭选中的标签
const closeSelectedTag = () => {
  closeTag(selectedTag.value)
}

// 关闭其他标签
const closeOtherTags = () => {
  tagsViewStore.closeOtherRoutes(selectedTag.value)
  appStore.cachedPages = [...tagsViewStore.cachedRoutes]

  if (!isActive(selectedTag.value)) {
    router.push(selectedTag.value.path)
  }
}

// 关闭左侧标签
const closeLeftTags = () => {
  tagsViewStore.closeLeftRoutes(selectedTag.value)
  appStore.cachedPages = [...tagsViewStore.cachedRoutes]
}

// 关闭右侧标签
const closeRightTags = () => {
  tagsViewStore.closeRightRoutes(selectedTag.value)
  appStore.cachedPages = [...tagsViewStore.cachedRoutes]
}

// 关闭所有标签
const closeAllTags = () => {
  tagsViewStore.closeAllRoutes()
  appStore.cachedPages = [...tagsViewStore.cachedRoutes]

  // 跳转到首页
  router.push('/home')
}
</script>

<style scoped lang="scss">
.tags-view-container {
  width: 100%;
  height: 34px;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  position: relative;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
}

.tags-scroll {
  width: 100%;
  flex: 1;
}

.tags-item {
  display: inline-flex;
  align-items: center;
  height: 26px;
  padding: 0 10px;
  margin: 4px 0 4px 6px;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 3px;
  color: var(--color-text);
  font-size: 12px;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;

  &:hover {
    color: var(--el-color-primary);
  }

  &.active {
    background: var(--el-color-primary);
    color: #fff;
    border-color: var(--el-color-primary);

    .close-icon:hover {
      background: rgba(255, 255, 255, 0.3);
    }
  }

  .tag-title {
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .close-icon {
    font-size: 12px;
    margin-left: 5px;
    border-radius: 50%;
    transition: background 0.2s;

    &:hover {
      background: rgba(0, 0, 0, 0.1);
      color: #fff;
    }
  }
}

.context-menu {
  position: fixed;
  background: var(--color-surface);
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 3000;
  padding: 5px 0;
  min-width: 120px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  cursor: pointer;
  color: var(--color-text);
  font-size: 13px;

  .el-icon {
    margin-right: 8px;
    font-size: 14px;
  }

  &:hover {
    background: var(--color-background);
    color: var(--el-color-primary);
  }
}
</style>