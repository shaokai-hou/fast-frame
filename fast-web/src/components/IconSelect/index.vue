<template>
  <el-popover
    v-model:visible="popoverVisible"
    trigger="click"
    :width="450"
    popper-class="icon-select-popover"
  >
    <!-- 触发器：显示已选图标或占位符 -->
    <template #reference>
      <div class="icon-input-wrapper">
        <el-input
          :model-value="modelValue"
          placeholder="请选择图标"
          readonly
          style="cursor: pointer"
        >
          <template #prefix>
            <el-icon
              v-if="modelValue"
              style="vertical-align: middle"
            >
              <component :is="modelValue" />
            </el-icon>
          </template>
          <template #suffix>
            <el-icon class="el-input__icon">
              <ArrowUp v-if="popoverVisible" />
              <ArrowDown v-else />
            </el-icon>
          </template>
        </el-input>
      </div>
    </template>

    <!-- 搜索框 -->
    <div class="icon-select-content">
      <el-input
        v-model="searchText"
        placeholder="搜索图标"
        clearable
        style="margin-bottom: 10px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <!-- 图标网格 -->
      <div class="icon-grid">
        <div
          v-for="icon in filteredIcons"
          :key="icon"
          class="icon-item"
          :class="{ active: modelValue === icon }"
          @click="selectIcon(icon)"
        >
          <el-icon :size="20">
            <component :is="icon" />
          </el-icon>
          <span class="icon-name">{{ icon }}</span>
        </div>
      </div>

      <!-- 无结果提示 -->
      <el-empty
        v-if="filteredIcons.length === 0"
        description="未找到匹配的图标"
        :image-size="60"
      />
    </div>
  </el-popover>
</template>

<script setup>
import { ref, computed } from 'vue'
import * as Icons from '@element-plus/icons-vue'
import { ArrowUp, ArrowDown, Search } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

// Popover 显示状态
const popoverVisible = ref(false)

// 搜索文本
const searchText = ref('')

// 所有图标名称列表
const iconList = Object.keys(Icons).filter(key => typeof Icons[key] === 'object')

// 过滤后的图标列表
const filteredIcons = computed(() => {
  if (!searchText.value) {
    return iconList
  }
  const searchLower = searchText.value.toLowerCase()
  return iconList.filter(icon =>
    icon.toLowerCase().includes(searchLower)
  )
})

// 选择图标
const selectIcon = (icon) => {
  // 安全修复：验证图标是否在预定义列表中，防止 XSS
  if (!iconList.includes(icon)) {
    return
  }
  emit('update:modelValue', icon)
  popoverVisible.value = false
  searchText.value = ''
}
</script>

<style scoped lang="scss">
.icon-input-wrapper {
  width: 100%;

  :deep(.el-input) {
    cursor: pointer;
    transition: border-color 0.2s, box-shadow 0.2s;

    &:hover {
      border-color: var(--color-primary-light);
    }

    &:focus-within {
      border-color: var(--color-primary);
      box-shadow: 0 0 0 2px var(--color-primary-lighter);
    }
  }
}

.icon-select-content {
  max-height: 350px;
  overflow-y: auto;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 4px;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s ease;

  &:hover {
    border-color: var(--color-primary-light);
    background-color: var(--color-primary-lighter);
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(15, 23, 42, 0.08);
  }

  &.active {
    border-color: var(--color-primary);
    background-color: var(--color-primary-lighter);

    .icon-name {
      color: var(--color-primary);
    }
  }
}

.icon-name {
  margin-top: 6px;
  font-size: 10px;
  color: var(--color-foreground-muted);
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}
</style>

<style>
.icon-select-popover {
  max-height: 400px;
}
</style>