<template>
  <component :is="type" v-bind="linkProps">
    <slot />
  </component>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  to: {
    type: [String, Object],
    required: true
  }
})

/**
 * 判断是否为外部链接
 */
function isExternal(path) {
  if (typeof path !== 'string') return false
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * 组件类型：外部链接用 a 标签，内部链接用 router-link
 */
const type = computed(() => {
  if (isExternal(props.to)) {
    return 'a'
  }
  return 'router-link'
})

/**
 * 组件属性
 */
const linkProps = computed(() => {
  if (isExternal(props.to)) {
    return {
      href: props.to,
      target: '_blank',
      rel: 'noopener'
    }
  }
  return {
    to: props.to
  }
})
</script>