<template>
  <div class="content-card">
    <!-- 工具栏 -->
    <div class="tool-bar">
      <el-button type="primary" plain :icon="Plus" @click="$emit('add')" v-hasPermi="['system:user:add']">新增</el-button>
      <el-button type="danger" plain :icon="Delete" @click="handleBatchDelete" :disabled="selection.length === 0"
        v-hasPermi="['system:user:delete']">删除</el-button>
      <el-button type="success" plain :icon="Download" @click="$emit('export')"
        v-hasPermi="['system:user:export']">导出</el-button>
      <el-button type="warning" plain :icon="Upload" @click="$emit('import')"
        v-hasPermi="['system:user:import']">导入</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="data" row-key="id" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" :selectable="row => row.id !== 1 && row.id !== '1'" />
      <el-table-column type="index" label="序号" width="60" align="center"
        :index="(index) => (pageNum - 1) * pageSize + index + 1" />
      <el-table-column label="用户名" prop="username" min-width="100" />
      <el-table-column label="昵称" prop="nickname" min-width="100" show-overflow-tooltip />
      <el-table-column label="部门" prop="deptName" min-width="100" show-overflow-tooltip />
      <el-table-column label="角色" min-width="120">
        <template #default="scope">
          <template v-if="scope.row.roles && scope.row.roles.length > 0">
            <el-tag v-for="role in scope.row.roles" :key="role.id" size="small" style="margin-right: 4px">
              {{ role.roleName }}
            </el-tag>
          </template>
          <span v-else style="color: #909399">未分配</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" prop="phone" min-width="120" />
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-switch v-model="scope.row.status" active-value="0" inactive-value="1"
            @change="handleStatusChange(scope.row)" :disabled="scope.row.id === 1 || scope.row.id === '1'" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="280" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="$emit('edit', scope.row)" :disabled="scope.row.id === 1 || scope.row.id === '1'"
            v-hasPermi="['system:user:edit']">修改</el-button>
          <el-button link type="warning" @click="$emit('resetPwd', scope.row)" :disabled="scope.row.id === 1 || scope.row.id === '1'"
            v-hasPermi="['system:user:resetPwd']">重置密码</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)" :disabled="scope.row.id === 1 || scope.row.id === '1'"
            v-hasPermi="['system:user:delete']">删除</el-button>
          <el-button link type="info" @click="$emit('unlock', scope.row)" v-if="scope.row.locked"
            v-hasPermi="['system:user:edit']">解锁</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page="pageNum"
      :limit="pageSize"
      @update:page="$emit('update:pageNum', $event)"
      @update:limit="$emit('update:pageSize', $event)"
      @pagination="$emit('refresh')"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Plus, Delete, Download, Upload } from '@element-plus/icons-vue'
import { changeStatus, deleteUser } from '@/api/system/user'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  total: {
    type: Number,
    default: 0
  },
  pageNum: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  }
})

const emit = defineEmits(['add', 'edit', 'delete', 'resetPwd', 'unlock', 'export', 'import', 'refresh'])

const selection = ref([])

// 多选
const handleSelectionChange = (val) => {
  selection.value = val.map(item => item.id)
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

// 单个删除
const handleDelete = async (row) => {
  await ElMessageBox.confirm('是否确认删除该用户?', '警告', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  emit('refresh')
}

// 批量删除
const handleBatchDelete = async () => {
  await ElMessageBox.confirm('是否确认删除选中的用户?', '警告', { type: 'warning' })
  await deleteUser(selection.value)
  ElMessage.success('删除成功')
  emit('refresh')
}
</script>