<template>
  <div class="page-container">
    <!-- 工具栏 -->
    <div class="tool-bar">
      <el-button type="primary" :icon="Refresh" @click="getInfo">刷新</el-button>
    </div>

    <!-- 服务器信息 -->
    <div class="info-grid">
      <!-- CPU信息 -->
      <div class="info-card">
        <div class="card-header">
          <span class="title">CPU</span>
          <el-tag type="success">{{ server.cpu?.core }} 核心</el-tag>
        </div>
        <div class="card-body">
          <div class="progress-item">
            <span class="label">使用率</span>
            <el-progress :percentage="server.cpu?.used || 0" :color="getProgressColor(server.cpu?.used)" />
          </div>
          <div class="stat-row">
            <span>已用: {{ server.cpu?.used }}%</span>
            <span>空闲: {{ server.cpu?.idle }}%</span>
          </div>
        </div>
      </div>

      <!-- 内存信息 -->
      <div class="info-card">
        <div class="card-header">
          <span class="title">内存</span>
          <el-tag type="warning">{{ server.mem?.total }} GB</el-tag>
        </div>
        <div class="card-body">
          <div class="progress-item">
            <span class="label">使用率</span>
            <el-progress :percentage="server.mem?.usage || 0" :color="getProgressColor(server.mem?.usage)" />
          </div>
          <div class="stat-row">
            <span>已用: {{ server.mem?.used }} GB</span>
            <span>空闲: {{ server.mem?.free }} GB</span>
          </div>
        </div>
      </div>

      <!-- JVM信息 -->
      <div class="info-card">
        <div class="card-header">
          <span class="title">JVM</span>
          <el-tag type="info">{{ server.jvm?.max }} MB</el-tag>
        </div>
        <div class="card-body">
          <div class="progress-item">
            <span class="label">使用率</span>
            <el-progress :percentage="server.jvm?.usage || 0" :color="getProgressColor(server.jvm?.usage)" />
          </div>
          <div class="stat-row">
            <span>已用: {{ server.jvm?.used }} MB</span>
            <span>空闲: {{ server.jvm?.free }} MB</span>
          </div>
          <div class="stat-row">
            <span>版本: {{ server.jvm?.version }}</span>
          </div>
        </div>
      </div>

      <!-- 系统信息 -->
      <div class="info-card">
        <div class="card-header">
          <span class="title">系统</span>
        </div>
        <div class="card-body">
          <div class="info-item">
            <span class="label">服务器名称</span>
            <span class="value">{{ server.sys?.hostName }}</span>
          </div>
          <div class="info-item">
            <span class="label">服务器IP</span>
            <span class="value">{{ server.sys?.ip }}</span>
          </div>
          <div class="info-item">
            <span class="label">操作系统</span>
            <span class="value">{{ server.sys?.osName }}</span>
          </div>
          <div class="info-item">
            <span class="label">系统架构</span>
            <span class="value">{{ server.sys?.osArch }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 磁盘信息 -->
    <div class="content-card" style="margin-top: 16px">
      <div class="card-title">磁盘信息</div>
      <el-table v-loading="loading" :data="server.sysFiles" row-key="dirName">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="磁盘名称" prop="dirName" show-overflow-tooltip />
        <el-table-column label="磁盘类型" prop="sysTypeName" width="120" />
        <el-table-column label="总大小(GB)" prop="total" width="120" />
        <el-table-column label="已用(GB)" prop="used" width="120" />
        <el-table-column label="剩余(GB)" prop="free" width="120" />
        <el-table-column label="使用率" prop="usage" width="150">
          <template #default="scope">
            <el-progress :percentage="scope.row.usage" :color="getProgressColor(scope.row.usage)" />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getServerInfo } from '@/api/monitor/server'

// 数据
const loading = ref(false)
const server = ref({
  cpu: {},
  mem: {},
  jvm: {},
  sys: {},
  sysFiles: []
})

// 获取服务器信息
const getInfo = async () => {
  loading.value = true
  try {
    const res = await getServerInfo()
    server.value = res.data
  } finally {
    loading.value = false
  }
}

// 根据百分比获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage < 50) return '#67c23a'
  if (percentage < 80) return '#e6a23c'
  return '#f56c6c'
}

onMounted(() => {
  getInfo()
})
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100%;
}

.tool-bar {
  margin-bottom: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.info-card {
  background: var(--color-surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .title {
      font-size: 18px;
      font-weight: 600;
      color: var(--color-text);
    }
  }

  .card-body {
    .progress-item {
      margin-bottom: 12px;

      .label {
        display: block;
        margin-bottom: 8px;
        color: var(--color-text-secondary);
      }
    }

    .stat-row {
      display: flex;
      justify-content: space-between;
      margin-top: 8px;
      color: var(--color-text-secondary);
      font-size: 14px;
    }

    .info-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;

      .label {
        color: var(--color-text-secondary);
      }

      .value {
        color: var(--color-text);
      }
    }
  }
}

.content-card {
  background: var(--color-surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);

  .card-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--color-text);
    margin-bottom: 16px;
  }
}

@media (max-width: 1200px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>