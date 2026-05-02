<template>
  <el-dialog :title="title" v-model="visible" width="600px" append-to-body @closed="resetForm">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
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
            <tree-select v-model="form.deptId" :data="deptOptions"
              :field-props="{ value: 'id', label: 'label', children: 'children' }" value-key="id" placeholder="请选择部门"
              check-strictly style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" placeholder="请选择性别">
              <el-option v-for="item in genderDict" :key="item.dictValue" :label="item.dictLabel"
                :value="item.dictValue" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio v-for="item in statusDict" :key="item.dictValue" :label="item.dictValue">{{ item.dictLabel }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="角色" prop="roleIds">
        <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
          <el-option v-for="role in filteredRoleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getUser, addUser, updateUser } from '@/api/system/user'
import { listAllRole } from '@/api/system/role'
import { getDeptTree } from '@/api/system/dept'
import { getDictData } from '@/api/system/dict'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  userId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(props.modelValue)
const loading = ref(false)
const formRef = ref(null)
const title = ref('新增用户')
const roleOptions = ref([])
const deptOptions = ref([])
const genderDict = ref([])
const statusDict = ref([])

// 过滤管理员角色（ID=1）
const filteredRoleOptions = computed(() => {
  return roleOptions.value.filter(role => role.id != 1)
})

const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  deptId: null,
  phone: '',
  email: '',
  gender: '0',
  status: '0',
  roleIds: [],
  remark: ''
})

const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  nickname: [{ required: true, message: '昵称不能为空', trigger: 'blur' }],
  roleIds: [{ required: true, message: '请选择角色', trigger: 'change', type: 'array' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }]
}

// 监听 visible
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    loadOptions()
    if (props.userId) {
      title.value = '修改用户'
      loadUserData(props.userId)
    } else {
      title.value = '新增用户'
      resetForm()
    }
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 加载选项数据
const loadOptions = async () => {
  const [roleRes, deptRes, genderRes, statusRes] = await Promise.all([
    listAllRole(),
    getDeptTree(),
    getDictData('sys_user_sex'),
    getDictData('sys_normal_disable')
  ])
  roleOptions.value = roleRes.data || []
  deptOptions.value = deptRes.data || []
  genderDict.value = genderRes.data || []
  statusDict.value = statusRes.data || []
}

// 加载用户数据
const loadUserData = async (id) => {
  const res = await getUser(id)
  Object.assign(form, res.data)
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    if (form.id) {
      await updateUser(form)
      ElMessage.success('修改成功')
    } else {
      await addUser(form)
      ElMessage.success('新增成功')
    }
    visible.value = false
    emit('success')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.username = ''
  form.password = ''
  form.nickname = ''
  form.deptId = null
  form.phone = ''
  form.email = ''
  form.gender = '0'
  form.status = '0'
  form.roleIds = []
  form.remark = ''
  formRef.value?.resetFields()
}
</script>