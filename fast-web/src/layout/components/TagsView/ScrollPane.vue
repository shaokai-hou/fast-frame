<template>
  <div class="scroll-pane-container">
    <!-- 左滚动按钮 -->
    <div
      v-show="showLeftBtn"
      class="scroll-btn left"
      @click="scrollLeft"
    >
      <el-icon><ArrowLeft /></el-icon>
    </div>

    <!-- 标签容器 -->
    <div
      ref="scrollWrapperRef"
      class="scroll-wrapper"
    >
      <div
        ref="scrollContentRef"
        class="scroll-content"
        :style="{ transform: `translateX(${offset}px)` }"
      >
        <slot />
      </div>
    </div>

    <!-- 右滚动按钮 -->
    <div
      v-show="showRightBtn"
      class="scroll-btn right"
      @click="scrollRight"
    >
      <el-icon><ArrowRight /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const scrollWrapperRef = ref(null)
const scrollContentRef = ref(null)
const offset = ref(0)
const showLeftBtn = ref(false)
const showRightBtn = ref(false)

// 计算滚动距离
const scrollWidth = ref(0)
const containerWidth = ref(0)

// ResizeObserver 引用
let resizeObserver = null

// 更新按钮显示状态
const updateScrollBtns = () => {
  if (!scrollContentRef.value || !scrollWrapperRef.value) return

  scrollWidth.value = scrollContentRef.value.offsetWidth
  containerWidth.value = scrollWrapperRef.value.offsetWidth

  // 内容不需要滚动时，重置 offset 并隐藏所有按钮
  if (scrollWidth.value <= containerWidth.value) {
    offset.value = 0
    showLeftBtn.value = false
    showRightBtn.value = false
    return
  }

  showLeftBtn.value = offset.value < 0
  showRightBtn.value = offset.value > containerWidth.value - scrollWidth.value
}

// 滚动到目标标签
const scrollToTag = (target) => {
  if (!scrollContentRef.value || !scrollWrapperRef.value) return

  const tags = scrollContentRef.value.querySelectorAll('.tags-item')
  if (!tags.length) return

  // 找到目标标签
  let targetTag = null
  tags.forEach(tag => {
    if (tag.dataset.path === target) {
      targetTag = tag
    }
  })

  if (!targetTag) return

  const tagLeft = targetTag.offsetLeft
  const tagWidth = targetTag.offsetWidth
  const containerW = containerWidth.value

  // 目标标签在右侧超出
  if (tagLeft + tagWidth > containerW + -offset.value) {
    offset.value = containerW - tagLeft - tagWidth - 10
  }
  // 目标标签在左侧超出
  if (tagLeft < -offset.value) {
    offset.value = -tagLeft + 10
  }

  updateScrollBtns()
}

// 左滚动
const scrollLeft = () => {
  offset.value += 100
  if (offset.value > 0) offset.value = 0
  updateScrollBtns()
}

// 右滚动
const scrollRight = () => {
  const maxOffset = containerWidth.value - scrollWidth.value
  offset.value -= 100
  if (offset.value < maxOffset) offset.value = maxOffset
  updateScrollBtns()
}

// 暴露方法给父组件
defineExpose({
  scrollToTag,
  updateScrollBtns
})

// 监听窗口变化
const handleResize = () => {
  updateScrollBtns()
}

onMounted(() => {
  // 使用 ResizeObserver 监听内容尺寸变化
  resizeObserver = new ResizeObserver(() => {
    updateScrollBtns()
  })

  nextTick(() => {
    if (scrollContentRef.value) {
      resizeObserver.observe(scrollContentRef.value)
    }
    updateScrollBtns()
  })

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (resizeObserver) {
    resizeObserver.disconnect()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.scroll-pane-container {
  display: flex;
  align-items: center;
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.scroll-btn {
  width: 28px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-surface);
  cursor: pointer;
  color: var(--color-text-secondary);
  z-index: 10;
  border-left: 1px solid var(--color-border);
  border-right: 1px solid var(--color-border);

  &:hover {
    color: var(--color-text);
    background: var(--color-background);
  }

  .el-icon {
    font-size: 14px;
  }
}

.scroll-wrapper {
  flex: 1;
  overflow: hidden;
  position: relative;
  height: 100%;
}

.scroll-content {
  display: inline-flex;
  height: 100%;
  transition: transform 0.2s ease;
  will-change: transform;
}
</style>