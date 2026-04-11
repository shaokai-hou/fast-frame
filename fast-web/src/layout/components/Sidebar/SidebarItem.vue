<template>
  <div v-if="!item.meta?.hidden">
    <!-- 没有子菜单 -->
    <template v-if="!hasChildren">
      <el-menu-item :index="resolvePath(item.path)">
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
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(item.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'

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

// 是否有子菜单
const hasChildren = computed(() => {
  const children = props.item.children || []
  const showingChildren = children.filter(child => !child.meta?.hidden)
  return showingChildren.length > 0
})

/**
 * 解析路径
 * 参考 vue-element-admin 的 path.resolve 实现
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