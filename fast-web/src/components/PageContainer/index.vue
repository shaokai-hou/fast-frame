<template>
  <div class="page-container" :style="containerStyle">
    <slot />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  padding: {
    type: [String, Boolean],
    default: true
  }
})

const containerStyle = computed(() => {
  if (props.padding === false) {
    return { padding: '0', minHeight: '100%' }
  }
  if (props.padding === true) {
    return { minHeight: '100%' }
  }
  return { padding: props.padding, minHeight: '100%' }
})
</script>

<style scoped>
.page-container {
  min-height: 100%;
}
</style>