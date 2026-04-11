<template>
  <el-switch
    v-model="isDark"
    inline-prompt
    :active-icon="Moon"
    :inactive-icon="Sunny"
    @change="toggleDark"
  />
</template>

<script setup>
import { computed } from 'vue'
import { Moon, Sunny } from '@element-plus/icons-vue'
import { useAppStore } from '@/store/app'

const appStore = useAppStore()

const isDark = computed({
  get: () => appStore.isDark,
  set: () => {}
})

function toggleDark() {
  appStore.toggleDark()
}
</script>