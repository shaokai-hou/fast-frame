<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar
      :model="queryParams"
      :visible="showSearch"
      @search="handleQuery"
      @reset="resetQuery"
    >
      <el-form-item
        label="用户名"
        prop="username"
      >
        <el-input
          v-model="queryParams.username"
          placeholder="请输入用户名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="onlineList"
        row-key="tokenId"
      >
        <el-table-column
          type="index"
          label="序号"
          width="60"
          align="center"
          :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1"
        />
        <el-table-column
          label="会话ID"
          prop="tokenId"
          width="400"
        />
        <el-table-column
          label="用户名"
          prop="username"
        />
        <el-table-column
          label="IP地址"
          prop="ip"
        />
        <el-table-column
          label="登录时间"
          prop="loginTime"
          width="180"
        />
        <el-table-column
          label="操作"
          align="center"
          width="120"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              v-hasPermi="['monitor:online:forceLogout']"
              link
              type="danger"
              @click="handleForceLogout(scope.row)"
            >
              强制退出
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </div>
  </PageContainer>
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
