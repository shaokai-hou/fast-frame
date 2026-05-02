<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" :visible="showSearch" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="操作人" prop="operName">
        <el-input v-model="queryParams.operName" placeholder="请输入操作人" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业务类型" prop="businessType">
        <el-select v-model="queryParams.businessType" placeholder="业务类型" clearable>
          <el-option v-for="item in businessTypeDict" :key="item.dictValue" :label="item.dictLabel"
            :value="parseInt(item.dictValue)" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="操作状态" clearable>
          <el-option label="成功" value="0" />
          <el-option label="失败" value="1" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="danger" plain :icon="Delete" @click="handleDelete" :disabled="multiple"
          v-hasPermi="['log:operlog:delete']">删除</el-button>
        <el-button type="danger" plain :icon="Delete" @click="handleClear"
          v-hasPermi="['log:operlog:delete']">清空</el-button>
        <el-button type="success" plain :icon="Download" @click="handleExport" v-hasPermi="['log:operlog:export']">导出</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="logList" row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center"
          :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="日志ID" prop="id" width="200" />
        <el-table-column label="操作模块" prop="title" show-overflow-tooltip />
        <el-table-column label="业务类型" align="center">
          <template #default="scope">
            <el-tag :type="getBusinessTypeTag(scope.row.businessType)">
              {{ getBusinessTypeLabel(scope.row.businessType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请求方式" prop="requestMethod" align="center" width="150">
          <template #default="scope">
            <el-tag :type="getMethodTag(scope.row.requestMethod)" size="small">
              {{ scope.row.requestMethod || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作人" prop="operName" />
        <el-table-column label="IP地址" prop="operIp" />
        <el-table-column label="状态" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作时间" prop="operTime" width="180" />
        <el-table-column label="操作" align="center" width="100" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)"
              v-hasPermi="['log:operlog:detail']">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 详情对话框 -->
    <el-dialog title="操作日志详情" v-model="detailOpen" width="1000px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作模块">{{ detailForm.title }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">
          <el-tag :type="getBusinessTypeTag(detailForm.businessType)">
            {{ getBusinessTypeLabel(detailForm.businessType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求方式">
          <el-tag :type="getMethodTag(detailForm.requestMethod)" size="small">
            {{ detailForm.requestMethod || '-' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailForm.operName }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailForm.operIp || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作地点">{{ detailForm.operLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="detailForm.status === '0' ? 'success' : 'danger'">
            {{ detailForm.status === '0' ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detailForm.operTime }}</el-descriptions-item>
        <el-descriptions-item label="追踪ID">
          <el-tag v-if="detailForm.traceId" type="info" size="small">{{ detailForm.traceId }}</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">{{ detailForm.method }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ detailForm.operUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre style="max-height: 200px; overflow: auto">{{ detailForm.operParam }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回结果" :span="2">
          <pre style="max-height: 200px; overflow: auto">{{ detailForm.jsonResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误消息" :span="2" v-if="detailForm.errorMsg">
          <pre style="max-height: 200px; overflow: auto; color: #f56c6c">{{ detailForm.errorMsg }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, Download } from '@element-plus/icons-vue'
import { listOperLog, getOperLog, deleteOperLog, clearOperLog, exportOperLog } from '@/api/log'
import { getDictData } from '@/api/system/dict'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const logList = ref([])
const ids = ref([])
const detailOpen = ref(false)
const detailForm = ref({})
const businessTypeDict = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  operName: undefined,
  businessType: undefined,
  status: undefined
})

// 标签样式映射
const businessTypeTagMap = {
  0: 'info',    // 其它
  1: 'success', // 新增
  2: 'warning', // 修改
  3: 'danger',  // 删除
  4: 'primary', // 授权
  5: 'success', // 导出
  6: 'success', // 导入
  7: 'danger',  // 强退
  8: 'danger'   // 清空
}

// 请求方法标签样式映射
const methodTagMap = {
  'GET': 'success',
  'POST': 'primary',
  'PUT': 'warning',
  'DELETE': 'danger',
  'PATCH': 'info'
}

// 获取业务类型标签
const getBusinessTypeLabel = (type) => {
  const item = businessTypeDict.value.find(d => parseInt(d.dictValue) === type)
  return item?.dictLabel || '未知'
}

// 获取业务类型标签样式
const getBusinessTypeTag = (type) => {
  return businessTypeTagMap[type] || 'info'
}

// 获取请求方法标签样式
const getMethodTag = (method) => {
  return methodTagMap[method] || 'info'
}

// 加载操作类型字典
const loadBusinessTypeDict = async () => {
  const res = await getDictData('sys_oper_type')
  businessTypeDict.value = res.data || []
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listOperLog(queryParams)
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
  queryParams.operName = undefined
  queryParams.businessType = undefined
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
  await ElMessageBox.confirm('是否确认导出操作日志数据?', '提示', { type: 'warning' })
  await exportOperLog(queryParams)
  ElMessage.success('导出成功')
}

// 查看详情
const handleView = async (row) => {
  const res = await getOperLog(row.id)
  detailForm.value = res.data
  detailOpen.value = true
}

// 删除
const handleDelete = async () => {
  await ElMessageBox.confirm('是否确认删除选中的操作日志?', '警告', { type: 'warning' })
  await deleteOperLog(ids.value)
  ElMessage.success('删除成功')
  getList()
}

// 清空
const handleClear = async () => {
  await ElMessageBox.confirm('是否确认清空所有操作日志?', '警告', { type: 'warning' })
  await clearOperLog()
  ElMessage.success('清空成功')
  getList()
}

onMounted(() => {
  loadBusinessTypeDict()
  getList()
})
</script>