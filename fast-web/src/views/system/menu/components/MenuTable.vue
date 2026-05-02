<template>
  <div class="content-card">
    <!-- 工具栏 -->
    <div class="tool-bar">
      <el-button type="primary" plain :icon="Plus" @click="$emit('add')" v-hasPermi="['system:menu:add']">新增</el-button>
      <el-button type="info" plain :icon="Sort" @click="toggleExpandAll">{{ isExpandAll ? '折叠' : '展开' }}</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table v-if="refreshTable" v-loading="loading" :data="data" row-key="id" :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column label="菜单名称" prop="menuName" show-overflow-tooltip />
      <el-table-column label="图标" align="center" width="80">
        <template #default="scope">
          <el-icon v-if="scope.row.icon">
            <component :is="scope.row.icon" />
          </el-icon>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="menuSort" width="80" />
      <el-table-column label="权限标识" prop="perms" />
      <el-table-column label="组件路径" prop="component" />
      <el-table-column label="外链地址" prop="link" />
      <el-table-column label="类型" align="center" width="80">
        <template #default="scope">
          <el-tag :type="getMenuTypeTag(scope.row.menuType)">
            {{ getMenuTypeLabel(scope.row.menuType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="$emit('edit', scope.row)"
            v-hasPermi="['system:menu:edit']">修改</el-button>
          <el-button link type="success" @click="$emit('add-child', scope.row)"
            v-hasPermi="['system:menu:add']">新增</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)"
            v-hasPermi="['system:menu:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Sort } from '@element-plus/icons-vue'
import { deleteMenu } from '@/api/system/menu'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  menuTypeDict: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['add', 'add-child', 'edit', 'refresh'])

const { proxy } = getCurrentInstance()
const isExpandAll = ref(false)
const refreshTable = ref(true)

const menuTypeTagMap = {
  D: 'primary',
  M: 'success',
  B: 'warning'
}

const getMenuTypeLabel = (type) => {
  const item = props.menuTypeDict.find(d => d.dictValue === type)
  return item?.dictLabel || '未知'
}

const getMenuTypeTag = (type) => {
  return menuTypeTagMap[type] || 'info'
}

const toggleExpandAll = () => {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  proxy.$nextTick(() => {
    refreshTable.value = true
  })
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('是否确认删除该菜单?', '警告', { type: 'warning' })
  await deleteMenu(row.id)
  ElMessage.success('删除成功')
  emit('refresh')
}
</script>