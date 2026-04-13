<template>
  <div
    v-for="item in appStore.iframeViews"
    :key="item.path"
    v-show="route.path === item.path"
    class="iframe-wrapper"
  >
    <iframe :src="item.meta.link" frameborder="0" style="width: 100%; height: 800px" />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useAppStore } from '@/store/app'

const route = useRoute()
const appStore = useAppStore()
</script>

<style scoped>
.iframe-wrapper {
  min-height: 100%;
}
</style>