<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" :visible="showSearch" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="任务名称" prop="jobName">
        <el-input v-model="queryParams.jobName" placeholder="请输入任务名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="任务分组" prop="jobGroup">
        <el-select v-model="queryParams.jobGroup" placeholder="全部" clearable>
          <el-option label="系统" value="SYSTEM" />
          <el-option label="业务" value="BUSINESS" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="正常" value="0" />
          <el-option label="暂停" value="1" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['monitor:job:add']">新增</el-button>
        <el-button type="danger" plain :icon="Delete" @click="handleDelete" :disabled="multiple" v-hasPermi="['monitor:job:delete']">删除</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="jobList" row-key="id" @selection-change="handleSelectionChange">
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
        <el-table-column label="Cron表达式" prop="cronExpression" width="150" />
        <el-table-column label="状态" prop="status" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
            <el-tag v-else type="warning">暂停</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['monitor:job:edit']">修改</el-button>
            <el-button link type="primary" @click="handleRun(scope.row)" v-hasPermi="['monitor:job:edit']">执行</el-button>
            <el-button link :type="scope.row.status === '0' ? 'warning' : 'success'" @click="handleStatusChange(scope.row)" v-hasPermi="['monitor:job:edit']">
              {{ scope.row.status === '0' ? '暂停' : '恢复' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['monitor:job:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="jobFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="任务名称" prop="jobName">
          <el-input v-model="form.jobName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务分组" prop="jobGroup">
          <el-select v-model="form.jobGroup" placeholder="请选择任务分组">
            <el-option label="系统" value="SYSTEM" />
            <el-option label="业务" value="BUSINESS" />
          </el-select>
        </el-form-item>
        <el-form-item label="调用目标" prop="invokeTarget">
          <el-input v-model="form.invokeTarget" placeholder="请输入调用目标字符串" />
          <div class="form-tip">格式：beanName.methodName 或 beanName.methodName(params)</div>
        </el-form-item>
        <el-form-item label="Cron表达式" prop="cronExpression">
          <el-input v-model="form.cronExpression" placeholder="请输入Cron表达式" @blur="handleCronValidate" />
          <div class="form-tip" :class="{ 'is-valid': cronValid }">
            {{ cronTip }}
          </div>
        </el-form-item>
        <el-form-item label="错过策略" prop="misfirePolicy">
          <el-select v-model="form.misfirePolicy" placeholder="请选择错过执行策略">
            <el-option label="立即执行" value="1" />
            <el-option label="执行一次" value="2" />
            <el-option label="放弃执行" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="并发执行" prop="concurrent">
          <el-radio-group v-model="form.concurrent">
            <el-radio value="0">允许</el-radio>
            <el-radio value="1">禁止</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { listJob, addJob, updateJob, deleteJob, changeJobStatus, runJob, checkCronExpression } from '@/api/monitor/job'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const jobList = ref([])
const title = ref('')
const open = ref(false)
const ids = ref([])
const cronValid = ref(false)
const cronTip = ref('')

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  jobName: undefined,
  jobGroup: undefined,
  status: undefined
})

// 表单
const form = ref({})
const rules = {
  jobName: [{ required: true, message: '任务名称不能为空', trigger: 'blur' }],
  jobGroup: [{ required: true, message: '任务分组不能为空', trigger: 'change' }],
  invokeTarget: [{ required: true, message: '调用目标字符串不能为空', trigger: 'blur' }],
  cronExpression: [{ required: true, message: 'Cron表达式不能为空', trigger: 'blur' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listJob(queryParams)
    jobList.value = res.data.records
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

// 新增
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '新增任务'
}

// 修改
const handleUpdate = async (row) => {
  reset()
  form.value = { ...row }
  open.value = true
  title.value = '修改任务'
  handleCronValidate()
}

// 状态切换
const handleStatusChange = async (row) => {
  const status = row.status === '0' ? '1' : '0'
  const text = row.status === '0' ? '暂停' : '恢复'
  await ElMessageBox.confirm(`确认要${text}任务"${row.jobName}"吗?`, '警告', { type: 'warning' })
  await changeJobStatus(row.id, status)
  ElMessage.success(`${text}成功`)
  getList()
}

// 立即执行
const handleRun = async (row) => {
  await ElMessageBox.confirm(`确认要立即执行一次任务"${row.jobName}"吗?`, '警告', { type: 'warning' })
  await runJob(row.id)
  ElMessage.success('执行成功')
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  await ElMessageBox.confirm('是否确认删除选中的任务?', '警告', { type: 'warning' })
  await deleteJob(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

// Cron表达式校验
const handleCronValidate = async () => {
  if (!form.value.cronExpression) {
    cronValid.value = false
    cronTip.value = '请输入Cron表达式'
    return
  }
  try {
    const res = await checkCronExpression(form.value.cronExpression)
    cronValid.value = res.data
    cronTip.value = res.data ? '表达式有效' : '表达式无效'
  } catch {
    cronValid.value = false
    cronTip.value = '表达式无效'
  }
}

// 提交表单
const submitForm = async () => {
  await proxy.$refs.jobFormRef.validate()
  if (!cronValid.value) {
    ElMessage.warning('Cron表达式格式错误')
    return
  }
  if (form.value.id) {
    await updateJob(form.value)
    ElMessage.success('修改成功')
  } else {
    await addJob(form.value)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

// 重置表单
const reset = () => {
  form.value = {
    id: undefined,
    jobName: undefined,
    jobGroup: 'SYSTEM',
    invokeTarget: undefined,
    cronExpression: undefined,
    misfirePolicy: '3',
    concurrent: '1',
    status: '0',
    remark: undefined
  }
  cronValid.value = false
  cronTip.value = ''
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;

  &.is-valid {
    color: #67c23a;
  }
}
</style>
