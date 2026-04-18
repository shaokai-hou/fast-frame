<template>
  <PageContainer>
    <!-- 顶部标题栏 -->
    <div class="header-bar">
      <el-button :icon="ArrowLeft" @click="handleBack">返回</el-button>
      <h2 class="page-title">字典数据 - {{ dictName }}</h2>
    </div>

    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" :visible="showSearch" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="字典标签" prop="dictLabel">
        <el-input v-model="queryParams.dictLabel" placeholder="请输入字典标签" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="字典键值" prop="dictValue">
        <el-input v-model="queryParams.dictValue" placeholder="请输入字典键值" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="字典状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="禁用" value="1" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:dict:add']">新增</el-button>
        <el-button type="danger" plain :icon="Delete" @click="handleDelete" :disabled="multiple" v-hasPermi="['system:dict:delete']">删除</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="dataList" row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="字典标签" prop="dictLabel" show-overflow-tooltip />
        <el-table-column label="字典键值" prop="dictValue" show-overflow-tooltip />
        <el-table-column label="排序" prop="dictSort" width="80" align="center" />
        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="150">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:dict:edit']">修改</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['system:dict:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dataFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典类型">
          <el-input v-model="dictName" disabled />
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="排序" prop="dictSort">
          <el-input-number v-model="form.dictSort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">禁用</el-radio>
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
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { listDictData, addDictData, updateDictData, deleteDictData } from '@/api/system/dict/data'

const router = useRouter()
const route = useRoute()
const { proxy } = getCurrentInstance()

// 从路由参数获取字典类型和名称
const dictType = ref(route.query.dictType || '')
const dictName = ref(route.query.dictName || '')

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const dataList = ref([])
const title = ref('')
const open = ref(false)
const ids = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  dictLabel: undefined,
  dictValue: undefined,
  status: undefined
})

// 表单
const form = ref({})
const rules = {
  dictLabel: [{ required: true, message: '字典标签不能为空', trigger: 'blur' }],
  dictValue: [{ required: true, message: '字典键值不能为空', trigger: 'blur' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listDictData({
      dictType: dictType.value,
      dictLabel: queryParams.dictLabel,
      dictValue: queryParams.dictValue,
      status: queryParams.status,
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    })
    dataList.value = res.data.records
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
  queryParams.dictLabel = undefined
  queryParams.dictValue = undefined
  queryParams.status = undefined
  handleQuery()
}

// 返回
const handleBack = () => {
  router.push('/system/dict')
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
  title.value = '新增字典数据'
}

// 修改
const handleUpdate = async (row) => {
  reset()
  form.value = { ...row }
  open.value = true
  title.value = '修改字典数据'
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  await ElMessageBox.confirm('是否确认删除选中的字典数据?', '警告', { type: 'warning' })
  await deleteDictData(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

// 提交表单
const submitForm = async () => {
  await proxy.$refs.dataFormRef.validate()
  const data = {
    ...form.value,
    dictType: dictType.value
  }

  if (form.value.id) {
    await updateDictData(data)
    ElMessage.success('修改成功')
  } else {
    await addDictData(data)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

// 重置表单
const reset = () => {
  form.value = {
    id: undefined,
    dictLabel: undefined,
    dictValue: undefined,
    dictSort: 0,
    status: '0',
    remark: undefined
  }
}

onMounted(() => {
  if (!dictType.value) {
    ElMessage.warning('缺少字典类型参数')
    router.push('/system/dict')
    return
  }
  getList()
})
</script>

<style scoped lang="scss">
.header-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;

  .page-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: var(--color-foreground);
  }
}
</style>
