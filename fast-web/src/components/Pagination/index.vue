<template>
  <div :class="{ 'hidden': hidden }" class="pagination-container">
    <el-pagination
      :background="background"
      v-model:current-page="currentPageValue"
      v-model:page-size="pageSizeValue"
      :layout="layout"
      :page-sizes="pageSizes"
      :total="total"
      v-bind="$attrs"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: {
    required: true,
    type: Number
  },
  page: {
    type: Number,
    default: 1
  },
  limit: {
    type: Number,
    default: 10
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 30, 50]
  },
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  background: {
    type: Boolean,
    default: true
  },
  hidden: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:page', 'update:limit', 'pagination'])

// 使用 computed 实现双向绑定
const currentPageValue = computed({
  get() {
    return props.page
  },
  set(val) {
    emit('update:page', val)
    emit('pagination', { page: val, limit: props.limit })
  }
})

const pageSizeValue = computed({
  get() {
    return props.limit
  },
  set(val) {
    emit('update:limit', val)
    emit('pagination', { page: props.page, limit: val })
  }
})
</script>

<style scoped lang="scss">
.pagination-container {
  background: transparent;
  padding: 12px 0;
  display: flex;
  justify-content: flex-end;

  :deep(.el-pagination) {
    gap: 8px;
  }

  :deep(.el-pagination .el-select) {
    width: 100px;
  }
}

.pagination-container.hidden {
  display: none;
}
</style>