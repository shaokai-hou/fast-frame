<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="用户状态" clearable>
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
        <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:user:add']">新增</el-button>
        <el-button type="danger" plain :icon="Delete" @click="handleDelete" :disabled="multiple" v-hasPermi="['system:user:delete']">删除</el-button>
        <el-button type="success" plain :icon="Download" @click="handleExport" v-hasPermi="['system:user:export']">导出</el-button>
        <el-button type="warning" plain :icon="Upload" @click="handleImport" v-hasPermi="['system:user:import']">导入</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange" class="data-table">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="用户名" prop="username" min-width="100" />
        <el-table-column label="昵称" prop="nickname" min-width="100" />
        <el-table-column label="部门" prop="deptName" min-width="100" />
        <el-table-column label="手机号" prop="phone" min-width="120" />
        <el-table-column label="邮箱" prop="email" min-width="160" />
        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:user:edit']">修改</el-button>
            <el-button link type="warning" @click="handleResetPwd(scope.row)" v-hasPermi="['system:user:resetPwd']">重置密码</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['system:user:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body class="form-dialog">
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" :disabled="form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="!form.id">
              <el-input v-model="form.password" type="password" placeholder="不填则使用初始密码" show-password />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname" v-else>
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname" v-if="!form.id">
              <el-input v-model="form.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone" v-else>
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone" v-if="!form.id">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email" v-else>
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <tree-select
                v-model="form.deptId"
                :data="deptOptions"
                :field-props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                placeholder="请选择部门"
                check-strictly
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别">
                <el-option label="未知" value="0" />
                <el-option label="男" value="1" />
                <el-option label="女" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
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

    <!-- 导入对话框 -->
    <el-dialog title="导入用户" v-model="importOpen" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :action="importUrl"
        :headers="uploadHeaders"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        :before-upload="beforeUpload"
        :auto-upload="false"
        accept=".xlsx"
        :limit="1"
      >
        <template #trigger>
          <el-button type="primary">选择文件</el-button>
        </template>
        <el-button type="success" style="margin-left: 12px" @click="submitUpload">开始导入</el-button>
        <template #tip>
          <div class="el-upload__tip">
            <el-button type="text" @click="handleDownloadTemplate">下载模板</el-button>
            <span style="margin-left: 8px">仅允许 .xlsx 文件</span>
          </div>
        </template>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download, Upload } from '@element-plus/icons-vue'
import { listUser, getUser, addUser, updateUser, deleteUser, resetPwd, changeStatus, exportUser, importUser, downloadTemplate } from '@/api/user'
import { listAllRole } from '@/api/role'
import { getDeptTree } from '@/api/dept'
import { getToken } from '@/utils/auth'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const userList = ref([])
const title = ref('')
const open = ref(false)
const roleOptions = ref([])
const deptOptions = ref([])
const ids = ref([])
const importOpen = ref(false)
const uploadRef = ref()

// 导入相关
const importUrl = computed(() => {
  return import.meta.env.VITE_API_URL + '/system/user/import'
})
const uploadHeaders = computed(() => {
  return { 'sa-token': getToken() }
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: undefined,
  phone: undefined,
  status: undefined
})

// 表单
const form = ref({})
const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
  roleIds: [{ required: true, message: '请选择角色', trigger: 'change', type: 'array' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listUser(queryParams)
    userList.value = res.data.records
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
  queryParams.phone = undefined
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
  const [roleRes, deptRes] = await Promise.all([listAllRole(), getDeptTree()])
  roleOptions.value = roleRes.data
  deptOptions.value = deptRes.data
  open.value = true
  title.value = '新增用户'
}

// 修改
const handleUpdate = async (row) => {
  reset()
  const [userRes, roleRes, deptRes] = await Promise.all([getUser(row.id), listAllRole(), getDeptTree()])
  roleOptions.value = roleRes.data
  deptOptions.value = deptRes.data
  form.value = { ...userRes.data }
  open.value = true
  title.value = '修改用户'
}

// 状态切换
const handleStatusChange = async (row) => {
  const text = row.status === '0' ? '启用' : '禁用'
  try {
    await changeStatus({ id: row.id, status: row.status })
    ElMessage.success(`${text}成功`)
  } catch {
    row.status = row.status === '0' ? '1' : '0'
  }
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  await ElMessageBox.confirm('是否确认删除选中的用户?', '警告', { type: 'warning' })
  await deleteUser(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

// 重置密码
const handleResetPwd = async (row) => {
  await ElMessageBox.confirm(
    `是否确认重置用户「${row.username}」的密码为初始密码？`,
    '提示',
    { type: 'warning' }
  )
  await resetPwd(row.id)
  ElMessage.success('重置密码成功')
}

// 提交表单
const submitForm = async () => {
  await proxy.$refs.userFormRef.validate()
  if (form.value.id) {
    await updateUser(form.value)
    ElMessage.success('修改成功')
  } else {
    await addUser(form.value)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

// 重置表单
const reset = () => {
  form.value = {
    id: undefined,
    username: undefined,
    password: undefined,
    nickname: undefined,
    deptId: undefined,
    phone: undefined,
    email: undefined,
    gender: '0',
    status: '0',
    roleIds: [],
    remark: undefined
  }
}

// 导出用户
const handleExport = async () => {
  try {
    const res = await exportUser(queryParams)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = '用户数据.xlsx'
    link.click()
    window.URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 打开导入对话框
const handleImport = () => {
  importOpen.value = true
}

// 文件上传前校验
const beforeUpload = (file) => {
  const isXlsx = file.name.endsWith('.xlsx')
  if (!isXlsx) {
    ElMessage.error('仅允许上传 .xlsx 文件')
    return false
  }
  return true
}

// 开始导入
const submitUpload = () => {
  uploadRef.value.submit()
}

// 导入成功
const handleImportSuccess = (response) => {
  importOpen.value = false
  if (response.code === 200) {
    const { successCount, errorCount, errorMessages } = response.data
    if (errorCount > 0) {
      ElMessageBox.alert(
        `成功导入 ${successCount} 条，失败 ${errorCount} 条。\n失败原因：\n${errorMessages.join('\n')}`,
        '导入结果',
        { type: 'warning' }
      )
    } else {
      ElMessage.success(`成功导入 ${successCount} 条数据`)
    }
    getList()
  } else {
    ElMessage.error(response.msg || '导入失败')
  }
}

// 导入失败
const handleImportError = () => {
  ElMessage.error('导入失败')
}

// 下载模板
const handleDownloadTemplate = async () => {
  try {
    const res = await downloadTemplate()
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = '用户导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(link.href)
  } catch (error) {
    ElMessage.error('下载模板失败')
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

.tool-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.data-table {
  width: 100%;
}

.form-dialog {
  :deep(.el-dialog__body) {
    padding: 24px;
  }
}
</style>
