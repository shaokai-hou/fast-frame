<template>
  <div
    class="screenfull"
    @click="toggle"
  >
    <el-icon :size="18">
      <FullScreen v-if="!isFullscreen" />
      <Aim v-else />
    </el-icon>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { FullScreen, Aim } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import screenfull from 'screenfull'

const isFullscreen = ref(false)

const toggle = () => {
  if (!screenfull.isEnabled) {
    ElMessage.warning('您的浏览器不支持全屏功能')
    return
  }
  screenfull.toggle()
}

const handleChange = () => {
  isFullscreen.value = screenfull.isFullscreen
}

onMounted(() => {
  if (screenfull.isEnabled) {
    screenfull.on('change', handleChange)
  }
})

onBeforeUnmount(() => {
  if (screenfull.isEnabled) {
    screenfull.off('change', handleChange)
  }
})
</script>

<style scoped lang="scss">
.screenfull {
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
</style>