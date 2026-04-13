<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item label="缓存前缀" prop="prefix">
          <el-select v-model="queryParams.prefix" placeholder="全部" clearable style="width: 200px">
            <el-option label="验证码" value="captcha_codes" />
            <el-option label="登录失败次数" value="login:fail" />
            <el-option label="登录锁定" value="login:lock" />
            <el-option label="Sa-Token Token" value="sa-token:login:token" />
            <el-option label="Sa-Token Session" value="sa-token:login:session" />
            <el-option label="字典数据" value="sys:dict" />
            <el-option label="系统配置" value="sys:config" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="danger" plain :icon="Delete" @click="handleClear" v-hasPermi="['monitor:cache:delete']">清空缓存</el-button>
        <el-button :icon="Refresh" @click="getList">刷新</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="cacheList">
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="缓存键名" prop="key" show-overflow-tooltip />
        <el-table-column label="缓存前缀" prop="prefix" width="150">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.prefix }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缓存类型" prop="type" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="过期时间(秒)" prop="ttl" width="120">
          <template #default="scope">
            <span v-if="scope.row.ttl === -1">永不过期</span>
            <span v-else-if="scope.row.ttl === -2">已过期</span>
            <span v-else>{{ scope.row.ttl }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['monitor:cache:query']">查看</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['monitor:cache:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <Pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 查看缓存内容对话框 -->
    <el-dialog title="缓存详情" v-model="viewOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="缓存键名">{{ cacheInfo.key }}</el-descriptions-item>
        <el-descriptions-item label="缓存类型">{{ cacheInfo.type }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ formatTtl(cacheInfo.ttl) }}</el-descriptions-item>
        <el-descriptions-item label="缓存大小">{{ cacheInfo.size }} 字节</el-descriptions-item>
        <el-descriptions-item label="缓存值" :span="2">
          <el-input v-model="cacheInfo.value" type="textarea" :rows="6" readonly />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { listCacheKeys, getCacheInfo, deleteCache, clearCache } from '@/api/monitor/cache'

// 数据
const loading = ref(false)
const total = ref(0)
const cacheList = ref([])
const viewOpen = ref(false)
const cacheInfo = ref({})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  prefix: undefined
})

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listCacheKeys(queryParams)
    cacheList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.prefix = undefined
  handleQuery()
}

// 查看缓存详情
const handleView = async (row) => {
  const res = await getCacheInfo(row.key)
  cacheInfo.value = res.data
  viewOpen.value = true
}

// 删除缓存
const handleDelete = async (row) => {
  await ElMessageBox.confirm(`是否确认删除缓存"${row.key}"?`, '警告', { type: 'warning' })
  await deleteCache(row.key)
  ElMessage.success('删除成功')
  getList()
}

// 清空缓存
const handleClear = async () => {
  await ElMessageBox.confirm('是否确认清空所有业务缓存?此操作将清除验证码、字典、配置等缓存数据。', '警告', { type: 'warning' })
  await clearCache()
  ElMessage.success('清空成功')
  getList()
}

// 获取类型标签样式
const getTypeTagType = (type) => {
  const typeMap = {
    string: 'success',
    hash: 'warning',
    list: 'info',
    set: 'primary',
    zset: 'danger'
  }
  return typeMap[type] || 'info'
}

// 格式化过期时间
const formatTtl = (ttl) => {
  if (ttl === -1) return '永不过期'
  if (ttl === -2) return '已过期'
  return `${ttl} 秒`
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100%;
}

.search-bar {
  background: var(--color-surface);
  padding: 20px 24px;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);

  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

.content-card {
  background: var(--color-surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
}

.tool-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}
</style>