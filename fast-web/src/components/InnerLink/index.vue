<template>
  <div
    v-for="item in appStore.iframeViews"
    v-show="route.path === item.path"
    :key="item.path"
    v-loading="loadingMap[item.path]"
    class="iframe-wrapper"
    element-loading-text="正在加载页面，请稍候..."
  >
    <iframe
      :src="item.meta?.link"
      frameborder="no"
      style="width: 100%; height: 100%"
      @load="onIframeLoad(item.path)"
    />
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/app'

const route = useRoute()
const appStore = useAppStore()

// iframe 加载状态映射
const loadingMap = reactive({})

// 监听 iframeViews 变化，初始化 loading 状态
watch(
  () => appStore.iframeViews,
  (views) => {
    views.forEach(view => {
      if (loadingMap[view.path] === undefined) {
        loadingMap[view.path] = true  // 新 iframe 默认加载中
      }
    })
  },
  { immediate: true, deep: true }
)

/**
 * iframe 加载完成回调
 */
function onIframeLoad(path) {
  loadingMap[path] = false
}
</script>

<style scoped>
.iframe-wrapper {
  min-height: 100%;
  height: calc(100vh - 94px);
}
</style>