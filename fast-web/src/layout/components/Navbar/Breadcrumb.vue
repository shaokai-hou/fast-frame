<template>
  <el-breadcrumb
    class="breadcrumb"
    separator="/"
  >
    <el-breadcrumb-item
      v-for="(item, index) in breadcrumbs"
      :key="item.path"
    >
      <!-- 目录类型：不可点击，灰色显示 -->
      <span
        v-if="isDirectory(item)"
        class="directory-item"
      >
        {{ item.meta?.title }}
      </span>
      <!-- 当前页面：不可点击，加重显示 -->
      <span
        v-else-if="index === breadcrumbs.length - 1"
        class="current-page"
      >
        {{ item.meta?.title }}
      </span>
      <!-- 叶子节点：可点击 -->
      <router-link
        v-else
        :to="item.path"
        class="redirect-link"
      >
        {{ item.meta?.title }}
      </router-link>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const breadcrumbs = ref([])

function getBreadcrumbs() {
  const matched = route.matched.filter(item => item.meta?.title)
  breadcrumbs.value = matched
}

// 判断是否为目录类型（有子菜单，无实际页面）
function isDirectory(routeItem) {
  return routeItem.children && routeItem.children.length > 0
}

watch(
  () => route.path,
  () => {
    getBreadcrumbs()
  },
  { immediate: true }
)
</script>

<style scoped lang="scss">
.breadcrumb {
  font-size: 14px;

  // 目录类型：不可点击，灰色显示
  .directory-item {
    color: var(--color-foreground-muted);
    cursor: default;
  }

  // 当前页面：不可点击，加重显示表示当前位置
  .current-page {
    color: var(--color-foreground);
    font-weight: 600;
    cursor: default;
  }

  // 可点击链接
  .redirect-link {
    color: var(--color-foreground-muted);
    cursor: pointer;
    transition: color 0.2s ease;

    &:hover {
      color: var(--color-primary);
    }
  }
}
</style>