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
      <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange" >
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
        <el-table-column label="操作" align="center" width="280" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:user:edit']">修改</el-button>
            <el-button link type="warning" @click="handleResetPwd(scope.row)" v-hasPermi="['system:user:resetPwd']">重置密码</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['system:user:delete']">删除</el-button>
            <el-button link type="info" @click="handleUnlock(scope.row)" v-if="scope.row.locked" v-hasPermi="['system:user:edit']">解锁</el-button>
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
    <el-dialog title="导入用户" v-model="importOpen" width="480px" append-to-body>
      <el-upload
        ref="uploadRef"
        :http-request="customUpload"
        :before-upload="beforeUpload"
        :auto-upload="false"
        accept=".xlsx"
        :limit="1"
        :file-list="fileList"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="upload-tip">
            <span class="link" @click="handleDownloadTemplate">下载模板</span>
            <span>仅允许 .xlsx 文件</span>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importOpen = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="importLoading" :disabled="fileList.length === 0">开始导入</el-button>
      </template>
    </el-dialog>

    <!-- 导入结果对话框 -->
    <el-dialog title="导入结果" v-model="resultOpen" width="480px" append-to-body class="result-dialog">
      <div class="result-content">
        <div class="result-stats">
          <span class="stat-item success">成功 <strong>{{ resultData.successCount }}</strong> 条</span>
          <span class="stat-item error">失败 <strong>{{ resultData.errorCount }}</strong> 条</span>
        </div>
        <el-table v-if="resultData.errorCount > 0" :data="parsedErrors" max-height="260" style="width: 100%; margin-top: 12px" border size="small">
          <el-table-column prop="row" label="行号" width="70" align="center" />
          <el-table-column prop="message" label="错误原因" show-overflow-tooltip />
        </el-table>
      </div>
      <template #footer>
        <el-button type="primary" @click="resultOpen = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download, Upload, UploadFilled } from '@element-plus/icons-vue'
import { listUser, getUser, addUser, updateUser, deleteUser, resetPwd, changeStatus, exportUser, importUser, downloadTemplate, unlockUser } from '@/api/system/user'
import { listAllRole } from '@/api/system/role'
import { getDeptTree } from '@/api/system/dept'

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
const importLoading = ref(false)
const fileList = ref([])
const resultOpen = ref(false)
const resultData = ref({
  successCount: 0,
  errorCount: 0,
  errorMessages: []
})

// 解析错误信息
const parsedErrors = computed(() => {
  return resultData.value.errorMessages.map(msg => {
    const match = msg.match(/第 (\d+) 行：(.+)/)
    if (match) {
      return { row: match[1], message: match[2] }
    }
    return { row: '-', message: msg }
  })
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

// 解锁用户
const handleUnlock = async (row) => {
  await ElMessageBox.confirm(
    `是否确认解锁用户「${row.username}」？`,
    '提示',
    { type: 'warning' }
  )
  await unlockUser(row.id)
  ElMessage.success('解锁成功')
  getList()
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
  fileList.value = []
  importOpen.value = true
}

// 文件选择变化
const handleFileChange = (file, files) => {
  fileList.value = files
}

// 文件移除
const handleFileRemove = (file, files) => {
  fileList.value = files
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

// 自定义上传方法
const customUpload = async (options) => {
  importLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const res = await importUser(formData)
    if (res.code === 200) {
      const { successCount, errorCount, errorMessages } = res.data
      importOpen.value = false
      resultData.value = { successCount, errorCount, errorMessages }
      resultOpen.value = true
      getList()
    } else {
      ElMessage.error(res.msg || '导入失败')
    }
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
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


.form-dialog {
  :deep(.el-dialog__body) {
    padding: 24px;
  }
}

.result-dialog {
  :deep(.el-dialog__body) {
    padding: 16px 20px;
  }
}

.result-content {
  .result-stats {
    display: flex;
    gap: 24px;
    font-size: 14px;

    .stat-item {
      &.success strong {
        color: #67c23a;
      }
      &.error strong {
        color: #e6a23c;
      }
    }
  }
}

.upload-tip {
  color: #909399;
  font-size: 13px;
  display: flex;
  gap: 8px;
  margin-top: 12px;

  .link {
    color: #409eff;
    cursor: pointer;

    &:hover {
      color: #66b1ff;
    }
  }
}
</style>
