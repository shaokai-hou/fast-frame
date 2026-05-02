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
        label="部门"
        prop="deptId"
      >
        <TreeSelect
          v-model="queryParams.deptId"
          :data="deptOptions"
          :field-props="{ label: 'label', children: 'children', value: 'id' }"
          value-key="id"
          clearable
          placeholder="请选择部门"
        />
      </el-form-item>
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
        label="手机号"
        prop="phone"
      >
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
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
          placeholder="用户状态"
          clearable
        >
          <el-option
            v-for="item in statusDict"
            :key="item.dictValue"
            :label="item.dictLabel"
            :value="item.dictValue"
          />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 用户表格 -->
    <UserTable
      :data="userList"
      :loading="loading"
      :total="total"
      :page-num="queryParams.pageNum"
      :page-size="queryParams.pageSize"
      @update:page-num="queryParams.pageNum = $event"
      @update:page-size="queryParams.pageSize = $event"
      @add="handleAdd"
      @edit="handleEdit"
      @delete="handleDelete"
      @reset-pwd="handleResetPwd"
      @unlock="handleUnlock"
      @export="handleExport"
      @import="handleImport"
      @refresh="getList"
    />

    <!-- 用户表单 -->
    <UserForm
      v-model="formVisible"
      :user-id="currentUserId"
      @success="getList"
    />

    <!-- 用户导入 -->
    <UserImport
      v-model="importVisible"
      @success="getList"
    />
  </PageContainer>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listUser, deleteUser, resetPwd, unlockUser, exportUser } from '@/api/system/user'
import { getDictData } from '@/api/system/dict'
import { getDeptTree } from '@/api/system/dept'
import UserTable from './components/UserTable.vue'
import UserForm from './components/UserForm.vue'
import UserImport from './components/UserImport.vue'

const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const userList = ref([])
const formVisible = ref(false)
const importVisible = ref(false)
const currentUserId = ref(null)
const statusDict = ref([])
const deptOptions = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  deptId: undefined,
  username: undefined,
  phone: undefined,
  status: undefined
})

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

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.deptId = undefined
  queryParams.username = undefined
  queryParams.phone = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleAdd = () => {
  currentUserId.value = null
  formVisible.value = true
}

const handleEdit = (row) => {
  currentUserId.value = row.id
  formVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('是否确认删除选中的用户?', '警告', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  getList()
}

const handleResetPwd = async (row) => {
  await ElMessageBox.confirm(
    `是否确认重置用户「${row.username}」的密码为初始密码？`,
    '提示',
    { type: 'warning' }
  )
  await resetPwd(row.id)
  ElMessage.success('重置密码成功')
}

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
  } catch {
    ElMessage.error('导出失败')
  }
}

const handleImport = () => {
  importVisible.value = true
}

const loadDictData = async () => {
  const res = await getDictData('sys_normal_disable')
  statusDict.value = res.data || []
}

const loadDeptTree = async () => {
  const res = await getDeptTree()
  deptOptions.value = res.data || []
}

onMounted(() => {
  getList()
  loadDictData()
  loadDeptTree()
})
</script>