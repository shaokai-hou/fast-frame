<template>
  <el-drawer
    v-model="visible"
    title="系统配置"
    direction="rtl"
    size="280px"
    :append-to-body="true"
  >
    <div class="settings-container">
      <!-- 界面设置 -->
      <div class="settings-section">
        <div class="section-title">界面设置</div>

        <div class="settings-item">
          <span>显示标签页</span>
          <el-switch
            :model-value="settingsStore.showTagsView"
            @change="settingsStore.toggleTagsView"
          />
        </div>

        <div class="settings-item">
          <span>显示侧边栏 Logo</span>
          <el-switch
            :model-value="settingsStore.showLogo"
            @change="settingsStore.toggleLogo"
          />
        </div>
      </div>

      <!-- 说明 -->
      <div class="settings-section">
        <div class="section-title">说明</div>
        <div class="description">
          <p>配置会自动保存到浏览器本地存储，刷新页面后仍然生效。</p>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref } from 'vue'
import { useSettingsStore } from '@/store/settings'

const settingsStore = useSettingsStore()

// 控制 drawer 显示
const visible = ref(false)

// 打开设置面板
const openSettings = () => {
  visible.value = true
}

// 关闭设置面板
const closeSettings = () => {
  visible.value = false
}

// 暴露方法给父组件
defineExpose({
  openSettings,
  closeSettings
})
</script>

<style scoped lang="scss">
.settings-container {
  padding: 0 20px;
}

.settings-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 12px;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 13px;
  color: var(--color-text);
}

.description {
  font-size: 12px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}
</style>