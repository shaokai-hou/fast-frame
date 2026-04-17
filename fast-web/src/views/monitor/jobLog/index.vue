<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="queryParams.jobName" placeholder="请输入任务名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="任务分组" prop="jobGroup">
          <el-select v-model="queryParams.jobGroup" placeholder="全部" clearable>
            <el-option label="系统" value="SYSTEM" />
            <el-option label="业务" value="BUSINESS" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="成功" value="0" />
            <el-option label="失败" value="1" />
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
        <el-button type="danger" plain :icon="Delete" @click="handleDelete" :disabled="multiple">删除</el-button>
        <el-button type="danger" plain :icon="Delete" @click="handleClear">清空</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="logList" row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="任务名称" prop="jobName" />
        <el-table-column label="任务分组" prop="jobGroup" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.jobGroup === 'SYSTEM'" type="danger">系统</el-tag>
            <el-tag v-else type="info">业务</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="调用目标" prop="invokeTarget" show-overflow-tooltip />
        <el-table-column label="日志信息" prop="jobMessage" show-overflow-tooltip />
        <el-table-column label="执行状态" prop="status" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="执行耗时" prop="duration" width="100">
          <template #default="scope">
            {{ scope.row.duration ? scope.row.duration + 'ms' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="开始时间" prop="startTime" width="180" />
        <el-table-column label="操作" align="center" width="100">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 详情对话框 -->
    <el-dialog title="日志详情" v-model="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="任务ID">{{ detailData.jobId }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ detailData.jobName }}</el-descriptions-item>
        <el-descriptions-item label="任务分组">
          <el-tag v-if="detailData.jobGroup === 'SYSTEM'" type="danger">系统</el-tag>
          <el-tag v-else type="info">业务</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="调用目标" :span="2">{{ detailData.invokeTarget }}</el-descriptions-item>
        <el-descriptions-item label="日志信息" :span="2">{{ detailData.jobMessage }}</el-descriptions-item>
        <el-descriptions-item label="执行状态">
          <el-tag v-if="detailData.status === '0'" type="success">成功</el-tag>
          <el-tag v-else type="danger">失败</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行耗时">{{ detailData.duration ? detailData.duration + 'ms' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detailData.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detailData.endTime }}</el-descriptions-item>
        <el-descriptions-item label="异常信息" :span="2">
          <div class="exception-info" v-if="detailData.exceptionInfo">{{ detailData.exceptionInfo }}</div>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { listJobLog, deleteJobLog, clearJobLog } from '@/api/monitor/job'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const logList = ref([])
const ids = ref([])
const detailOpen = ref(false)
const detailData = ref({})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  jobName: undefined,
  jobGroup: undefined,
  status: undefined
})

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listJobLog(queryParams)
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
  queryParams.jobName = undefined
  queryParams.jobGroup = undefined
  queryParams.status = undefined
  handleQuery()
}

// 多选
const handleSelectionChange = (selection) => {
  ids.value = selection.map((item) => item.id)
  multiple.value = !selection.length
}

// 查看详情
const handleView = (row) => {
  detailData.value = { ...row }
  detailOpen.value = true
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  await ElMessageBox.confirm('是否确认删除选中的日志?', '警告', { type: 'warning' })
  await deleteJobLog(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

// 清空
const handleClear = async () => {
  await ElMessageBox.confirm('是否确认清空所有日志?', '警告', { type: 'warning' })
  await clearJobLog()
  ElMessage.success('清空成功')
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.page-container {
  min-height: 100%;
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

.exception-info {
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  color: #f56c6c;
}
</style>