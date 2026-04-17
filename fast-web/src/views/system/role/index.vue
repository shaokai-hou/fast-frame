<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="queryParams.roleKey" placeholder="请输入权限字符" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="角色状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="禁用" value="1" />
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
        <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:role:add']">新增</el-button>
        <el-button type="danger" plain :icon="Delete" @click="handleDelete" :disabled="multiple" v-hasPermi="['system:role:delete']">删除</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="roleList" row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" :selectable="row => row.id !== '1'" />
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="角色名称" prop="roleName" min-width="120" />
        <el-table-column label="权限字符" prop="roleKey" min-width="120" show-overflow-tooltip />
        <el-table-column label="数据权限" align="center" width="120">
          <template #default="scope">
            <el-tag effect="plain">
              {{ scope.row.dataScope === '1' ? '全部数据' : scope.row.dataScope === '2' ? '自定义' : scope.row.dataScope === '3' ? '本部门' : scope.row.dataScope === '4' ? '本部门及以下' : '仅本人' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="显示顺序" prop="roleSort" width="100" />
        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" effect="light">
              {{ scope.row.status === '0' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" :disabled="scope.row.id === '1'" v-hasPermi="['system:role:edit']">修改</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" :disabled="scope.row.id === '1'" v-hasPermi="['system:role:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body class="form-dialog">
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="form.roleName" placeholder="请输入角色名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限字符" prop="roleKey">
              <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="roleSort">
              <el-input-number v-model="form.roleSort" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据权限" prop="dataScope">
              <el-select v-model="form.dataScope" placeholder="请选择数据权限" style="width: 100%">
                <el-option label="全部数据" value="1" />
                <el-option label="自定义数据" value="2" />
                <el-option label="本部门数据" value="3" />
                <el-option label="本部门及以下" value="4" />
                <el-option label="仅本人数据" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="数据部门" prop="deptIds" v-if="form.dataScope === '2'">
          <TreeSelect
            v-model="form.deptIds"
            :data="deptOptions"
            :field-props="{ label: 'label', children: 'children', value: 'id' }"
            value-key="id"
            multiple
            show-checkbox
            check-on-click-node
            default-expand-all
            placeholder="请选择数据部门"
          />
        </el-form-item>
        <el-form-item label="菜单权限" prop="menuIds">
          <TreeSelect
            v-model="form.menuIds"
            :data="menuOptions"
            :field-props="{ label: 'label', children: 'children', value: 'id' }"
            value-key="id"
            multiple
            show-checkbox
            check-on-click-node
            default-expand-all
            placeholder="请选择菜单权限"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import TreeSelect from '@/components/TreeSelect/index.vue'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { listRole, getRole, addRole, updateRole, deleteRole, getMenuTree, getRoleMenuIds } from '@/api/system/role'
import { getDeptTree, getRoleDeptIds } from '@/api/system/dept'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const roleList = ref([])
const title = ref('')
const open = ref(false)
const menuOptions = ref([])
const deptOptions = ref([])
const ids = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: undefined,
  roleKey: undefined,
  status: undefined
})

// 表单
const form = ref({})
const rules = {
  roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
  roleKey: [{ required: true, message: '权限字符不能为空', trigger: 'blur' }],
  roleSort: [{ required: true, message: '显示顺序不能为空', trigger: 'blur' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listRole(queryParams)
    roleList.value = res.data.records
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
  queryParams.roleName = undefined
  queryParams.roleKey = undefined
  queryParams.status = undefined
  handleQuery()
}

// 多选
const handleSelectionChange = (selection) => {
  ids.value = selection.map((item) => item.id)
  multiple.value = !selection.length
}

// 新增
const handleAdd = async () => {
  reset()
  const [menuRes, deptRes] = await Promise.all([getMenuTree(), getDeptTree()])
  menuOptions.value = menuRes.data
  deptOptions.value = deptRes.data
  open.value = true
  title.value = '新增角色'
}

// 修改
const handleUpdate = async (row) => {
  reset()
  const [roleRes, menuTreeRes, menuIdsRes, deptRes] = await Promise.all([
    getRole(row.id),
    getMenuTree(),
    getRoleMenuIds(row.id),
    getDeptTree()
  ])
  menuOptions.value = menuTreeRes.data
  deptOptions.value = deptRes.data
  form.value = {
    ...roleRes.data,
    menuIds: menuIdsRes.data || []
  }
  // 如果是自定义数据权限，加载角色部门
  if (roleRes.data.dataScope === '2') {
    const deptIdsRes = await getRoleDeptIds(row.id)
    form.value.deptIds = deptIdsRes.data || []
  }
  open.value = true
  title.value = '修改角色'
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  await ElMessageBox.confirm('是否确认删除选中的角色?', '警告', { type: 'warning' })
  await deleteRole(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

// 提交表单
const submitForm = async () => {
  await proxy.$refs.roleFormRef.validate()
  // form.menuIds 和 form.deptIds 已是 TreeSelect 返回的数组
  if (form.value.id) {
    await updateRole(form.value)
    ElMessage.success('修改成功')
  } else {
    await addRole(form.value)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

// 重置表单
const reset = () => {
  form.value = {
    id: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: '0',
    dataScope: '1',
    menuIds: [],
    deptIds: [],
    remark: undefined
  }
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