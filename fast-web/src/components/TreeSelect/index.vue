<template>
  <el-tree-select
    :model-value="modelValue"
    :data="data"
    :props="fieldProps"
    :value-key="valueKey"
    :node-key="valueKey"
    :placeholder="placeholder"
    :clearable="clearable"
    :multiple="multiple"
    :show-checkbox="showCheckbox"
    :check-on-click-node="checkOnClickNode"
    :default-expand-all="defaultExpandAll"
    :render-after-expand="false"
    check-strictly
    v-bind="$attrs"
    @update:model-value="emit('update:modelValue', $event)"
  />
</template>

<script setup>
defineProps({
  modelValue: {
    type: [String, Number, Array],
    default: ''
  },
  data: {
    type: Array,
    default: () => []
  },
  multiple: {
    type: Boolean,
    default: false
  },
  showCheckbox: {
    type: Boolean,
    default: false
  },
  checkOnClickNode: {
    type: Boolean,
    default: false
  },
  defaultExpandAll: {
    type: Boolean,
    default: false
  },
  fieldProps: {
    type: Object,
    default: () => ({})
  },
  valueKey: {
    type: String,
    default: 'value'
  },
  placeholder: {
    type: String,
    default: '请选择'
  },
  clearable: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue'])
</script>

<style scoped lang="scss">
:deep(.el-tree-select) {
  width: 100%;
}

:deep(.el-tree-node__content) {
  height: 36px;
  border-radius: 6px;
  transition: background-color 0.2s;

  &:hover {
    background-color: var(--color-primary-lighter);
  }
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: var(--color-primary-lighter);
  font-weight: 500;
  color: var(--color-primary);
}
</style>