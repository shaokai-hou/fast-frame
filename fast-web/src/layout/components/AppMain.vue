<template>
  <section class="app-main">
    <router-view v-slot="{ Component, route }">
      <transition
        v-if="!route.meta?.link"
        name="slide-fade"
        mode="out-in"
      >
        <keep-alive :include="appStore.cachedPages">
          <component
            :is="Component"
            :key="route.fullPath"
          />
        </keep-alive>
      </transition>
    </router-view>
    <!-- iframe 外链页面 -->
    <InnerLink />
  </section>
</template>

<script setup>
import { watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/app'
import InnerLink from '@/components/InnerLink/index.vue'

const route = useRoute()
const appStore = useAppStore()

// 监听路由变化，添加 iframe 视图
watch(
  route,
  (newRoute) => {
    if (newRoute.meta?.link) {
      appStore.addIframeView(newRoute)
    }
  }
)
</script>

<style scoped lang="scss">
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