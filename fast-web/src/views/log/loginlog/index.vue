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
      <el-form-item
        label="状态"
        prop="status"
      >
        <el-select
          v-model="queryParams.status"
          placeholder="登录状态"
          clearable
        >
          <el-option
            label="成功"
            value="0"
          />
          <el-option
            label="失败"
            value="1"
          />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button
          v-hasPermi="['log:loginlog:delete']"
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
        >
          删除
        </el-button>
        <el-button
          v-hasPermi="['log:loginlog:delete']"
          type="danger"
          plain
          :icon="Delete"
          @click="handleClear"
        >
          清空
        </el-button>
        <el-button
          v-hasPermi="['log:loginlog:export']"
          type="success"
          plain
          :icon="Download"
          @click="handleExport"
        >
          导出
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="logList"
        row-key="id"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
        />
        <el-table-column
          type="index"
          label="序号"
          width="60"
          align="center"
          :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1"
        />
        <el-table-column
          label="访问ID"
          prop="id"
          width="200"
        />
        <el-table-column
          label="用户名"
          prop="username"
        />
        <el-table-column
          label="IP地址"
          prop="ipAddress"
        />
        <el-table-column
          label="状态"
          align="center"
        >
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="提示消息"
          prop="msg"
        />
        <el-table-column
          label="登录时间"
          prop="loginTime"
          width="180"
        />
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
import { Search, Refresh, Delete, Download } from '@element-plus/icons-vue'
import { listLoginLog, deleteLoginLog, clearLoginLog, exportLoginLog } from '@/api/log'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const logList = ref([])
const ids = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: undefined,
  status: undefined
})

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listLoginLog(queryParams)
    logList.value = res.data.records
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
  queryParams.status = undefined
  handleQuery()
}

// 多选
const handleSelectionChange = (selection) => {
  ids.value = selection.map((item) => item.id)
  multiple.value = !selection.length
}

// 导出
const handleExport = async () => {
  await ElMessageBox.confirm('是否确认导出登录日志数据?', '提示', { type: 'warning' })
  await exportLoginLog(queryParams)
  ElMessage.success('导出成功')
}

// 删除
const handleDelete = async () => {
  await ElMessageBox.confirm('是否确认删除选中的登录日志?', '警告', { type: 'warning' })
  await deleteLoginLog(ids.value)
  ElMessage.success('删除成功')
  getList()
}

// 清空
const handleClear = async () => {
  await ElMessageBox.confirm('是否确认清空所有登录日志?', '警告', { type: 'warning' })
  await clearLoginLog()
  ElMessage.success('清空成功')
  getList()
}

// 使用 onMounted 确保只在组件挂载后调用一次
onMounted(() => {
  getList()
})
</script>

