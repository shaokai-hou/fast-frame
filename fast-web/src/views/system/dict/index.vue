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
        label="字典名称"
        prop="dictName"
      >
        <el-input
          v-model="queryParams.dictName"
          placeholder="请输入字典名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="字典类型"
        prop="dictType"
      >
        <el-input
          v-model="queryParams.dictType"
          placeholder="请输入字典类型"
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
          placeholder="字典状态"
          clearable
        >
          <el-option
            label="正常"
            value="0"
          />
          <el-option
            label="禁用"
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
          v-hasPermi="['system:dict:add']"
          type="primary"
          plain
          :icon="Plus"
          @click="handleAdd"
        >
          新增
        </el-button>
        <el-button
          v-hasPermi="['system:dict:delete']"
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
        >
          删除
        </el-button>
        <el-button
          v-hasPermi="['system:dict:edit']"
          type="warning"
          plain
          :icon="RefreshRight"
          @click="handleRefreshCache"
        >
          刷新缓存
        </el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="dictList"
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
          label="字典名称"
          prop="dictName"
          width="200"
          show-overflow-tooltip
        />
        <el-table-column
          label="字典类型"
          prop="dictType"
          width="200"
          show-overflow-tooltip
        />
        <el-table-column
          label="状态"
          align="center"
          width="120"
        >
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="备注"
          prop="remark"
          show-overflow-tooltip
        />
        <el-table-column
          label="创建时间"
          prop="createTime"
          width="180"
        />
        <el-table-column
          label="操作"
          align="center"
          width="250"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              v-hasPermi="['system:dict:page']"
              link
              type="primary"
              @click="handleDictData(scope.row)"
            >
              字典数据
            </el-button>
            <el-button
              v-hasPermi="['system:dict:edit']"
              link
              type="primary"
              @click="handleUpdate(scope.row)"
            >
              修改
            </el-button>
            <el-button
              v-hasPermi="['system:dict:delete']"
              link
              type="danger"
              @click="handleDelete(scope.row)"
            >
              删除
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

    <!-- 新增/修改对话框 -->
    <el-dialog
      v-model="open"
      :title="title"
      width="500px"
      append-to-body
    >
      <el-form
        ref="dictFormRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item
          label="字典名称"
          prop="dictName"
        >
          <el-input
            v-model="form.dictName"
            placeholder="请输入字典名称"
          />
        </el-form-item>
        <el-form-item
          label="字典类型"
          prop="dictType"
        >
          <el-input
            v-model="form.dictType"
            placeholder="请输入字典类型"
          />
        </el-form-item>
        <el-form-item
          label="状态"
          prop="status"
        >
          <el-radio-group v-model="form.status">
            <el-radio label="0">
              正常
            </el-radio>
            <el-radio label="1">
              禁用
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="备注"
          prop="remark"
        >
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="submitForm"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, RefreshRight } from '@element-plus/icons-vue'
import { listDictType, addDictType, updateDictType, deleteDictType, refreshDictCache } from '@/api/system/dict'

const router = useRouter()
const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const dictList = ref([])
const title = ref('')
const open = ref(false)
const ids = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  dictName: undefined,
  dictType: undefined,
  status: undefined
})

// 表单
const form = ref({})
const rules = {
  dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }],
  dictType: [{ required: true, message: '字典类型不能为空', trigger: 'blur' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listDictType(queryParams)
    dictList.value = res.data.records
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
  queryParams.dictName = undefined
  queryParams.dictType = undefined
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
  title.value = '新增字典'
}

// 查看字典数据
const handleDictData = (row) => {
  router.push({
    path: '/system/dict/data',
    query: {
      dictType: row.dictType,
      dictName: row.dictName
    }
  })
}

// 修改
const handleUpdate = async (row) => {
  reset()
  form.value = { ...row }
  open.value = true
  title.value = '修改字典'
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  await ElMessageBox.confirm('是否确认删除选中的字典?', '警告', { type: 'warning' })
  await deleteDictType(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

// 提交表单
const submitForm = async () => {
  await proxy.$refs.dictFormRef.validate()
  if (form.value.id) {
    await updateDictType(form.value)
    ElMessage.success('修改成功')
  } else {
    await addDictType(form.value)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

// 重置表单
const reset = () => {
  form.value = {
    id: undefined,
    dictName: undefined,
    dictType: undefined,
    status: '0',
    remark: undefined
  }
}

// 刷新缓存
const handleRefreshCache = async () => {
  await ElMessageBox.confirm('确认要刷新字典缓存吗？', '提示', { type: 'warning' })
  await refreshDictCache()
  ElMessage.success('刷新缓存成功')
}

// 使用 onMounted 确保只在组件挂载后调用一次
onMounted(() => {
  getList()
})
</script>
