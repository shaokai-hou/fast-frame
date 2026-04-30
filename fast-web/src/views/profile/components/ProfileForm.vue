<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="80px"
    class="profile-form"
  >
    <el-form-item label="昵称" prop="nickname">
      <el-input v-model="form.nickname" placeholder="请输入昵称" />
    </el-form-item>
    <el-form-item label="手机号" prop="phone">
      <el-input v-model="form.phone" placeholder="请输入手机号" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" placeholder="请输入邮箱" />
    </el-form-item>
    <el-form-item label="性别" prop="gender">
      <el-select v-model="form.gender" placeholder="请选择性别">
        <el-option label="未知" value="0" />
        <el-option label="男" value="1" />
        <el-option label="女" value="2" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        保存修改
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updateProfile } from '@/api/profile'

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['updated'])

const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  nickname: '',
  phone: '',
  email: '',
  gender: '0'
})

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }]
}

// 监听初始数据变化
watch(
  () => props.initialData,
  (data) => {
    if (data) {
      form.nickname = data.nickname || ''
      form.phone = data.phone || ''
      form.email = data.email || ''
      form.gender = data.gender || '0'
    }
  },
  { immediate: true }
)

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await updateProfile({
      nickname: form.nickname,
      phone: form.phone,
      email: form.email,
      gender: form.gender
    })
    userStore.updateUserInfo({
      nickname: form.nickname,
      phone: form.phone,
      email: form.email,
      gender: form.gender
    })
    emit('updated', { ...form })
    ElMessage.success('修改成功')
  } catch (e) {
    // 错误由请求拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.profile-form {
  :deep(.el-form-item__label) {
    color: var(--color-foreground);
  }

  :deep(.el-input),
  :deep(.el-select) {
    width: 100%;
  }
}
</style>