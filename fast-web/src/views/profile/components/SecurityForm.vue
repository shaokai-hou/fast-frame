<template>
  <div class="security-section">
    <h3 class="section-title">
      修改密码
    </h3>
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="password-form"
    >
      <el-form-item
        label="当前密码"
        prop="oldPassword"
      >
        <el-input
          v-model="form.oldPassword"
          type="password"
          placeholder="请输入当前密码"
          show-password
        />
      </el-form-item>
      <el-form-item
        label="新密码"
        prop="newPassword"
      >
        <el-input
          v-model="form.newPassword"
          type="password"
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
      <el-form-item
        label="确认新密码"
        prop="confirmPassword"
      >
        <el-input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :loading="loading"
          @click="handleSubmit"
        >
          修改密码
        </el-button>
        <el-button @click="resetForm">
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updatePassword } from '@/api/profile'

const emit = defineEmits(['password-changed'])

const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await updatePassword({
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    resetForm()
    emit('password-changed')
    // 延迟后退出登录
    setTimeout(() => {
      userStore.logoutAction()
    }, 1500)
  } catch (e) {
    // 错误由请求拦截器处理
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.oldPassword = ''
  form.newPassword = ''
  form.confirmPassword = ''
  formRef.value?.resetFields()
}
</script>

<style scoped lang="scss">
.security-section {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-foreground);
    margin: 0 0 24px 0;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--color-border-light);
  }
}

.password-form {
  :deep(.el-form-item__label) {
    color: var(--color-foreground);
  }

  :deep(.el-input) {
    width: 100%;
  }
}
</style>