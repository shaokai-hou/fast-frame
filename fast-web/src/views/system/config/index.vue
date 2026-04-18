<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" :visible="showSearch" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="参数名称" prop="configName">
        <el-input v-model="queryParams.configName" placeholder="请输入参数名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input v-model="queryParams.configKey" placeholder="请输入参数键名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="参数类型" prop="configType">
        <el-select v-model="queryParams.configType" placeholder="全部" clearable>
          <el-option label="系统内置" value="0" />
          <el-option label="其他" value="1" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:config:add']">新增</el-button>
        <el-button type="danger" plain :icon="Delete" @click="handleDelete" :disabled="multiple" v-hasPermi="['system:config:delete']">删除</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="configList" row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="参数名称" prop="configName" min-width="100" show-overflow-tooltip />
        <el-table-column label="参数键名" prop="configKey" min-width="100" show-overflow-tooltip />
        <el-table-column label="参数键值" prop="configValue" min-width="120" show-overflow-tooltip />
        <el-table-column label="类型" prop="configType" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.configType === '0'" type="danger">系统内置</el-tag>
            <el-tag v-else type="info">其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="100" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:config:edit']">修改</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['system:config:delete']" :disabled="scope.row.configType === '0'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="configFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" placeholder="请输入参数键值" />
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
import { listConfig, addConfig, updateConfig, deleteConfig } from '@/api/system/config'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const configList = ref([])
const title = ref('')
const open = ref(false)
const ids = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  configName: undefined,
  configKey: undefined,
  configType: undefined
})

// 表单
const form = ref({})
const rules = {
  configName: [{ required: true, message: '参数名称不能为空', trigger: 'blur' }],
  configKey: [{ required: true, message: '参数键名不能为空', trigger: 'blur' }],
  configValue: [{ required: true, message: '参数键值不能为空', trigger: 'blur' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listConfig(queryParams)
    configList.value = res.data.records
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
  queryParams.configName = undefined
  queryParams.configKey = undefined
  queryParams.configType = undefined
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
  title.value = '新增参数'
}

// 修改
const handleUpdate = async (row) => {
  reset()
  form.value = { ...row }
  open.value = true
  title.value = '修改参数'
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  // 单条删除时，检查是否为系统参数
  if (row.id && row.configType === '0') {
    ElMessage.warning('系统内置参数不允许删除')
    return
  }
  // 批量删除时，先过滤系统参数
  if (!row.id) {
    const selectedRows = configList.value.filter(item => ids.value.includes(item.id))
    const hasSystemConfig = selectedRows.some(item => item.configType === '0')
    if (hasSystemConfig) {
      ElMessage.warning('选中的参数包含系统内置参数，不允许删除')
      return
    }
  }
  await ElMessageBox.confirm('是否确认删除选中的参数?', '警告', { type: 'warning' })
  await deleteConfig(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

// 提交表单
const submitForm = async () => {
  await proxy.$refs.configFormRef.validate()
  if (form.value.id) {
    await updateConfig(form.value)
    ElMessage.success('修改成功')
  } else {
    await addConfig(form.value)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

// 重置表单
const reset = () => {
  form.value = {
    id: undefined,
    configName: undefined,
    configKey: undefined,
    configValue: undefined,
    configType: '1',
    remark: undefined
  }
}

// 使用 onMounted 确保只在组件挂载后调用一次
onMounted(() => {
  getList()
})
</script>

