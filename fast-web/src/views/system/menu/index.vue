<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="禁用" value="1" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 菜单表格 -->
    <MenuTable
      :data="menuList"
      :loading="loading"
      :menu-type-dict="menuTypeDict"
      @add="handleAdd"
      @add-child="handleAddChild"
      @edit="handleEdit"
      @refresh="getList"
    />

    <!-- 菜单表单 -->
    <MenuForm v-model="formVisible" :menu-id="currentMenuId" :parent-id="currentParentId" @success="getList" />
  </PageContainer>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listMenu } from '@/api/system/menu'
import { getDictData } from '@/api/system/dict'
import MenuTable from './components/MenuTable.vue'
import MenuForm from './components/MenuForm.vue'

const loading = ref(false)
const menuList = ref([])
const menuTypeDict = ref([])
const formVisible = ref(false)
const currentMenuId = ref(null)
const currentParentId = ref(0)

const queryParams = reactive({
  menuName: undefined,
  status: undefined
})

const handleTree = (data, id, parentId, children) => {
  const config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children'
  }
  const childrenListMap = {}
  const nodeIds = {}
  const tree = []

  for (const d of data) {
    const pId = d[config.parentId]
    if (!childrenListMap[pId]) {
      childrenListMap[pId] = []
    }
    nodeIds[d[config.id]] = d
    childrenListMap[pId].push(d)
  }

  for (const d of data) {
    const pId = d[config.parentId]
    if (!nodeIds[pId]) {
      tree.push(d)
    }
  }

  for (const t of tree) {
    adaptToChildren(t)
  }

  function adaptToChildren(o) {
    if (childrenListMap[o[config.id]]) {
      o[config.childrenList] = childrenListMap[o[config.id]]
    }
    if (o[config.childrenList]) {
      for (const c of o[config.childrenList]) {
        adaptToChildren(c)
      }
    }
  }
  return tree
}

const getList = async () => {
  loading.value = true
  try {
    const res = await listMenu(queryParams)
    menuList.value = handleTree(res.data, 'id', 'parentId')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.menuName = undefined
  queryParams.status = undefined
  handleQuery()
}

const handleAdd = () => {
  currentMenuId.value = null
  currentParentId.value = 0
  formVisible.value = true
}

const handleAddChild = (row) => {
  currentMenuId.value = null
  currentParentId.value = row.id
  formVisible.value = true
}

const handleEdit = (row) => {
  currentMenuId.value = row.id
  currentParentId.value = 0
  formVisible.value = true
}

const loadMenuTypeDict = async () => {
  try {
    const res = await getDictData('sys_menu_type')
    menuTypeDict.value = res.data || []
  } catch {
    // 加载失败不影响主流程
  }
}

onMounted(() => {
  loadMenuTypeDict()
  getList()
})
</script>