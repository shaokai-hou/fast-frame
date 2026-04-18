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

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd" v-hasPermi="['system:menu:add']">新增</el-button>
        <el-button type="info" plain :icon="Sort" @click="toggleExpandAll">{{ isExpandAll ? '折叠' : '展开' }}</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="menuList"
      row-key="id"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column label="菜单名称" prop="menuName" />
      <el-table-column label="图标" align="center" width="80">
        <template #default="scope">
          <el-icon v-if="scope.row.icon"><component :is="scope.row.icon" /></el-icon>
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
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['system:menu:edit']">修改</el-button>
          <el-button link type="success" @click="handleAdd(scope.row)" v-hasPermi="['system:menu:add']">新增</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['system:menu:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="menuFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单" prop="parentId">
              <tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :field-props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                placeholder="选择上级菜单"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item prop="menuType">
              <template #label>
                <el-tooltip content="目录：包含子菜单的文件夹；菜单：实际页面；按钮：页面内操作按钮" placement="top">
                  <el-icon class="label-tip"><QuestionFilled /></el-icon>
                </el-tooltip>
                菜单类型
              </template>
              <el-radio-group v-model="form.menuType">
                <el-radio
                  v-for="item in menuTypeDict"
                  :key="item.dictValue"
                  :label="item.dictValue"
                >
                  {{ item.dictLabel }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单图标" prop="icon">
              <IconSelect v-model="form.icon" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.menuType !== 'B'">
          <el-col :span="12">
            <el-form-item prop="path">
              <template #label>
                <el-tooltip content="前端路由路径，如：/system/user（目录类型可不填）" placement="top">
                  <el-icon class="label-tip"><QuestionFilled /></el-icon>
                </el-tooltip>
                路由地址
              </template>
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="component">
              <template #label>
                <el-tooltip content="Vue组件路径，如：system/user/index（目录类型可不填）" placement="top">
                  <el-icon class="label-tip"><QuestionFilled /></el-icon>
                </el-tooltip>
                组件路径
              </template>
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.menuType === 'M'">
          <el-col :span="24">
            <el-form-item prop="link">
              <template #label>
                <el-tooltip content="iframe外链地址，如：/api/druid/index.html（仅菜单类型）" placement="top">
                  <el-icon class="label-tip"><QuestionFilled /></el-icon>
                </el-tooltip>
                外链地址
              </template>
              <el-input v-model="form.link" placeholder="如果是iframe页面，填写外链地址，如：/api/druid/index.html" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item prop="perms">
              <template #label>
                <el-tooltip content="权限控制标识，如：system:user:list，多个用逗号分隔" placement="top">
                  <el-icon class="label-tip"><QuestionFilled /></el-icon>
                </el-tooltip>
                权限标识
              </template>
              <el-input v-model="form.perms" placeholder="请输入权限标识" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="menuSort">
              <el-input-number v-model="form.menuSort" :min="0" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.menuType !== 'B'">
          <el-col :span="12">
            <el-form-item label="是否显示" prop="visible">
              <el-radio-group v-model="form.visible">
                <el-radio label="0">显示</el-radio>
                <el-radio label="1">隐藏</el-radio>
              </el-radio-group>
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
import { Search, Refresh, Plus, Sort, QuestionFilled } from '@element-plus/icons-vue'
import { listMenu, getMenu, addMenu, updateMenu, deleteMenu, getMenuTree } from '@/api/system/menu'
import { getDictData } from '@/api/system/dict'
import IconSelect from '@/components/IconSelect/index.vue'

const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const menuList = ref([])
const title = ref('')
const open = ref(false)
const isExpandAll = ref(false)
const refreshTable = ref(true)
const menuOptions = ref([])
const menuTypeDict = ref([])

// 菜单类型标签样式映射
const menuTypeTagMap = {
  D: 'primary',  // 目录
  M: 'success',  // 菜单
  B: 'warning'   // 按钮
}

// 获取菜单类型标签
const getMenuTypeLabel = (type) => {
  const item = menuTypeDict.value.find(d => d.dictValue === type)
  return item?.dictLabel || '未知'
}

// 获取菜单类型标签样式
const getMenuTypeTag = (type) => {
  return menuTypeTagMap[type] || 'info'
}

// 加载菜单类型字典
const loadMenuTypeDict = async () => {
  try {
    const res = await getDictData('sys_menu_type')
    menuTypeDict.value = res.data || []
  } catch (e) {
    // 加载失败不影响主流程
  }
}

// 查询参数
const queryParams = reactive({
  menuName: undefined,
  status: undefined
})

// 表单
const form = ref({})
const rules = {
  menuName: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
  menuSort: [{ required: true, message: '显示顺序不能为空', trigger: 'blur' }]
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listMenu(queryParams)
    menuList.value = handleTree(res.data, 'id', 'parentId')
  } finally {
    loading.value = false
  }
}

// 树形数据处理
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

// 搜索
const handleQuery = () => {
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.menuName = undefined
  queryParams.status = undefined
  handleQuery()
}

// 展开/折叠
const toggleExpandAll = () => {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  proxy.$nextTick(() => {
    refreshTable.value = true
  })
}

// 新增
const handleAdd = async (row) => {
  reset()
  const res = await getMenuTree()
  menuOptions.value = [{ id: 0, label: '主类目', children: res.data }]
  if (row) {
    form.value.parentId = row.id
  } else {
    form.value.parentId = 0
  }
  open.value = true
  title.value = '新增菜单'
}

// 修改
const handleUpdate = async (row) => {
  reset()
  const [menuRes, treeRes] = await Promise.all([getMenu(row.id), getMenuTree()])
  menuOptions.value = [{ id: 0, label: '主类目', children: treeRes.data }]
  form.value = { ...menuRes.data }
  open.value = true
  title.value = '修改菜单'
}

// 删除
const handleDelete = async (row) => {
  await ElMessageBox.confirm('是否确认删除该菜单?', '警告', { type: 'warning' })
  await deleteMenu(row.id)
  ElMessage.success('删除成功')
  getList()
}

// 提交表单
const submitForm = async () => {
  await proxy.$refs.menuFormRef.validate()
  if (form.value.id) {
    await updateMenu(form.value)
    ElMessage.success('修改成功')
  } else {
    await addMenu(form.value)
    ElMessage.success('新增成功')
  }
  open.value = false
  getList()
}

// 重置表单
const reset = () => {
  form.value = {
    id: undefined,
    parentId: 0,
    menuName: undefined,
    menuType: 'M',
    path: undefined,
    component: undefined,
    perms: undefined,
    icon: undefined,
    link: undefined,
    menuSort: 0,
    visible: '0',
    status: '0'
  }
}

// 使用 onMounted 确保只在组件挂载后调用一次
onMounted(() => {
  loadMenuTypeDict()
  getList()
})
</script>

<style scoped lang="scss">
.label-tip {
  margin-right: 4px;
  color: var(--el-color-info);
  cursor: help;
  font-size: 14px;
  vertical-align: middle;
}

// 确保带 tooltip 的 label 与其他 label 对齐
:deep(.el-form-item__label) {
  display: flex;
  align-items: center;
}
</style>

