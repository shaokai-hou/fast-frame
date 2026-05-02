<template>
  <div
    v-show="visible"
    class="search-bar"
  >
    <el-form
      ref="formRef"
      :model="model"
      :inline="true"
      class="search-form"
    >
      <slot />
      <el-form-item>
        <el-button
          type="primary"
          :icon="Search"
          @click="handleSearch"
        >
          搜索
        </el-button>
        <el-button
          :icon="Refresh"
          @click="handleReset"
        >
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  model: {
    type: Object,
    default: () => ({})
  },
  visible: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['search', 'reset'])
const formRef = ref(null)

function handleSearch() {
  emit('search')
}

function handleReset() {
  emit('reset')
}

defineExpose({
  formRef
})
</script>

<style scoped lang="scss">
.search-bar {
  background: var(--color-surface);
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
  margin-bottom: 16px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 16px;

    &:last-child {
      margin-right: 0;
    }
  }

  :deep(.el-input),
  :deep(.el-select) {
    width: 200px;
  }
}
</style>