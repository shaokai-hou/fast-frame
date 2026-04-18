<template>
  <div v-if="!item.meta?.hidden">
    <!-- 只有一个可显示的子菜单：直接显示子菜单（跳过父级） -->
    <template v-if="hasOneShowingChild(item.children, item) && (!onlyOneChild.children || onlyOneChild.noShowingChildren)">
      <app-link v-if="onlyOneChild.meta" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)">
          <el-icon v-if="onlyOneChild.meta?.icon || item.meta?.icon">
            <component :is="onlyOneChild.meta?.icon || item.meta?.icon" />
          </el-icon>
          <template #title>{{ onlyOneChild.meta?.title }}</template>
        </el-menu-item>
      </app-link>
    </template>

    <!-- 有多个子菜单或需要显示父级：显示为子菜单 -->
    <el-sub-menu v-else :index="resolvePath(item.path)">
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <SidebarItem
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(item.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import AppLink from './Link.vue'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

// 存储唯一的子菜单（用于单子菜单情况）
const onlyOneChild = ref(null)

/**
 * 判断是否只有一个可显示的子菜单
 * @param children 子菜单列表
 * @param parent 父菜单
 * @returns boolean
 */
function hasOneShowingChild(children = [], parent) {
  if (!children) {
    children = []
  }

  // 过滤出可显示的子菜单
  const showingChildren = children.filter(child => {
    if (child.meta?.hidden) {
      return false
    }
    // 暂存唯一的子菜单（会在后续使用）
    onlyOneChild.value = child
    return true
  })

  // 只有一个可显示的子菜单时，直接显示该子菜单
  if (showingChildren.length === 1) {
    return true
  }

  // 没有可显示的子菜单时，显示父菜单本身（标记为无子菜单显示）
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  // 多个子菜单，返回 false，显示为子菜单结构
  return false
}

/**
 * 解析路径
 */
function resolvePath(routePath) {
  // 外部链接直接返回
  if (isExternal(routePath)) {
    return routePath
  }
  // 外部 basePath 直接返回
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  // 绝对路径直接返回
  if (routePath.startsWith('/')) {
    return routePath
  }
  // 拼接路径
  const base = props.basePath || ''
  if (!base) {
    return '/' + routePath
  }
  return base.endsWith('/') ? base + routePath : base + '/' + routePath
}

/**
 * 判断是否为外部链接
 */
function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}
</script>