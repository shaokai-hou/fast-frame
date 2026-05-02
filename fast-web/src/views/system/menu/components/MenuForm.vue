<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="680px"
    append-to-body
    @closed="resetForm"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
    >
      <el-row>
        <el-col :span="24">
          <el-form-item
            label="上级菜单"
            prop="parentId"
          >
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
              <el-tooltip
                content="目录：包含子菜单的文件夹；菜单：实际页面；按钮：页面内操作按钮"
                placement="top"
              >
                <el-icon class="label-tip">
                  <QuestionFilled />
                </el-icon>
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
          <el-form-item
            label="菜单名称"
            prop="menuName"
          >
            <el-input
              v-model="form.menuName"
              placeholder="请输入菜单名称"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="菜单图标"
            prop="icon"
          >
            <IconSelect v-model="form.icon" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="form.menuType === 'M'">
        <el-col :span="12">
          <el-form-item prop="path">
            <template #label>
              <el-tooltip
                content="前端路由路径，如：/system/user"
                placement="top"
              >
                <el-icon class="label-tip">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
              路由地址
            </template>
            <el-input
              v-model="form.path"
              placeholder="请输入路由地址"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="component">
            <template #label>
              <el-tooltip
                content="Vue组件路径，如：system/user/index"
                placement="top"
              >
                <el-icon class="label-tip">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
              组件路径
            </template>
            <el-input
              v-model="form.component"
              placeholder="请输入组件路径"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="form.menuType === 'M'">
        <el-col :span="24">
          <el-form-item prop="link">
            <template #label>
              <el-tooltip
                content="iframe外链地址，如：/api/druid/index.html"
                placement="top"
              >
                <el-icon class="label-tip">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
              外链地址
            </template>
            <el-input
              v-model="form.link"
              placeholder="如果是iframe页面，填写外链地址，如：/api/druid/index.html"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item prop="perms">
            <template #label>
              <el-tooltip
                content="权限控制标识，如：system:user:list，多个用逗号分隔"
                placement="top"
              >
                <el-icon class="label-tip">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
              权限标识
            </template>
            <el-input
              v-model="form.perms"
              placeholder="请输入权限标识"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="显示顺序"
            prop="menuSort"
          >
            <el-input-number
              v-model="form.menuSort"
              :min="0"
              controls-position="right"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="form.menuType !== 'B'">
        <el-col :span="12">
          <el-form-item
            label="是否显示"
            prop="visible"
          >
            <el-radio-group v-model="form.visible">
              <el-radio label="0">
                显示
              </el-radio>
              <el-radio label="1">
                隐藏
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
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
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">
        取消
      </el-button>
      <el-button
        type="primary"
        :loading="loading"
        @click="handleSubmit"
      >
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue'
import { getMenu, addMenu, updateMenu, getMenuTree } from '@/api/system/menu'
import { getDictData } from '@/api/system/dict'
import IconSelect from '@/components/IconSelect/index.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  menuId: {
    type: Number,
    default: null
  },
  parentId: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(props.modelValue)
const loading = ref(false)
const formRef = ref(null)
const title = ref('新增菜单')
const menuOptions = ref([])
const menuTypeDict = ref([])

const form = reactive({
  id: null,
  parentId: 0,
  menuName: '',
  menuType: 'M',
  path: '',
  component: '',
  perms: '',
  icon: '',
  link: '',
  menuSort: 0,
  visible: '0',
  status: '0'
})

const rules = computed(() => {
  const baseRules = {
    menuName: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
    menuSort: [{ required: true, message: '显示顺序不能为空', trigger: 'blur' }]
  }
  if (form.menuType === 'M') {
    baseRules.path = [{ required: true, message: '路由地址不能为空', trigger: 'blur' }]
    baseRules.component = [{ required: true, message: '组件路径不能为空', trigger: 'blur' }]
  }
  if (form.menuType === 'B') {
    baseRules.perms = [{ required: true, message: '权限标识不能为空', trigger: 'blur' }]
  }
  return baseRules
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    loadOptions()
    loadMenuTypeDict()
    if (props.menuId) {
      title.value = '修改菜单'
      loadMenuData(props.menuId)
    } else {
      title.value = '新增菜单'
      form.parentId = props.parentId
    }
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const loadOptions = async () => {
  const res = await getMenuTree()
  menuOptions.value = [{ id: 0, label: '主类目', children: res.data }]
}

const loadMenuTypeDict = async () => {
  const res = await getDictData('sys_menu_type')
  menuTypeDict.value = res.data || []
}

const loadMenuData = async (id) => {
  const res = await getMenu(id)
  Object.assign(form, res.data)
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    if (form.id) {
      await updateMenu(form)
      ElMessage.success('修改成功')
    } else {
      await addMenu(form)
      ElMessage.success('新增成功')
    }
    visible.value = false
    emit('success')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.id = null
  form.parentId = 0
  form.menuName = ''
  form.menuType = 'M'
  form.path = ''
  form.component = ''
  form.perms = ''
  form.icon = ''
  form.link = ''
  form.menuSort = 0
  form.visible = '0'
  form.status = '0'
  formRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.label-tip {
  margin-right: 4px;
  color: var(--el-color-info);
  cursor: help;
  font-size: 14px;
  vertical-align: middle;
}

:deep(.el-form-item__label) {
  display: flex;
  align-items: center;
}
</style>