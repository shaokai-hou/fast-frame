<template>
  <div class="designer-container">
    <iframe
      :src="designerUrl"
      frameborder="0"
      style="width: 100%; height: calc(100vh - 100px);"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { getToken } from '@/utils/auth'

const route = useRoute()

// 流程定义ID (编辑时传入)
const definitionId = route.query.id || ''

// 是否只显示设计器
const onlyDesignShow = route.query.onlyDesignShow || ''

// 是否禁用编辑
const disabled = route.query.disabled || ''

const designerUrl = computed(() => {
  const baseUrl = `/api/warm-flow-ui/index.html`
  const params = new URLSearchParams()

  if (definitionId) {
    params.set('id', definitionId)
  }
  if (onlyDesignShow) {
    params.set('onlyDesignShow', onlyDesignShow)
  }
  if (disabled) {
    params.set('disabled', disabled)
  }

  // 添加 token 以共享权限
  const token = getToken()
  if (token) {
    params.set('sa-token', token)
  }

  return `${baseUrl}?${params.toString()}`
})
</script>

<style scoped>
.designer-container {
  padding: 0;
  margin: 0;
  height: 100%;
}
</style>