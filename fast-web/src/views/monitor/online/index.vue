<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 数据表格 -->
    <el-table v-loading="loading" :data="onlineList">
      <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
      <el-table-column label="会话ID" prop="tokenId" width="400" />
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="IP地址" prop="ip" />
      <el-table-column label="登录时间" prop="loginTime" width="180" />
      <el-table-column label="操作" align="center" width="120" fixed="right">
        <template #default="scope">
          <el-button link type="danger" @click="handleForceLogout(scope.row)"
                     v-hasPermi="['monitor:online:forceLogout']">强制退出</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { listOnlineUsers, forceLogout } from '@/api/monitor/online'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const onlineList = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: undefined
})

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listOnlineUsers(queryParams)
    onlineList.value = res.data.records
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
  queryParams.username = undefined
  handleQuery()
}

// 强制退出
const handleForceLogout = async (row) => {
  await ElMessageBox.confirm(`是否确认强制退出用户【${row.username}】?`, '警告', { type: 'warning' })
  await forceLogout(row.tokenId)
  ElMessage.success('强制退出成功')
  getList()
}

// 使用 onMounted 确保只在组件挂载后调用一次
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

  :deep(.el-input),
  :deep(.el-select) {
    width: 200px;
  }
}

.content-card {
  background: var(--color-surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
}
</style>