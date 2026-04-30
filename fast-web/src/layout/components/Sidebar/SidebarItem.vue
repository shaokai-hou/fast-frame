<template>
  <div v-if="!item.meta?.hidden">
    <!-- 没有子菜单 -->
    <template v-if="!hasChildren">
      <el-menu-item :index="resolvePath(item.path)" @click="handleMenuClick(resolvePath(item.path))">
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <template #title>{{ item.meta?.title }}</template>
      </el-menu-item>
    </template>

    <!-- 有子菜单 -->
    <el-sub-menu v-else :index="resolvePath(item.path)">
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <SidebarItem
        v-for="child in visibleChildren"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(item.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

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

const router = useRouter()

/**
 * 过滤出可显示的子菜单
 */
const visibleChildren = computed(() => {
  const children = props.item.children || []
  return children.filter(child => !child.meta?.hidden)
})

/**
 * 是否有子菜单
 */
const hasChildren = computed(() => {
  return visibleChildren.value.length > 0
})

/**
 * 判断是否为外部链接
 */
function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * 处理菜单点击
 */
function handleMenuClick(path) {
  if (isExternal(path)) {
    window.open(path, '_blank', 'noopener')
  } else {
    router.push(path)
  }
}

/**
 * 解析路径
 */
function resolvePath(routePath) {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  if (routePath.startsWith('/')) {
    return routePath
  }
  const base = props.basePath || ''
  if (!base) {
    return '/' + routePath
  }
  return base.endsWith('/') ? base + routePath : base + '/' + routePath
}
</script>