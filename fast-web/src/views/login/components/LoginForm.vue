<template>
  <div class="login-form-wrapper">
    <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
      <el-form-item prop="username">
        <el-input
          v-model="form.username"
          placeholder="请输入用户名"
          prefix-icon="User"
          size="large"
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          prefix-icon="Lock"
          size="large"
          show-password
          @keyup.enter="handleLogin"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          class="login-btn"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>

  <!-- 滑块验证码弹窗 -->
  <Verify
    ref="verifyRef"
    mode="pop"
    captchaType="blockPuzzle"
    @success="onVerifySuccess"
    @error="onVerifyError"
  />
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import Verify from './Verify.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const verifyRef = ref(null)
const loading = ref(false)
const captchaVerification = ref('')
const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 点击登录按钮
async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // 显示滑块验证码弹窗
  verifyRef.value.show()
}

// 滑块验证成功
async function onVerifySuccess(params) {
  captchaVerification.value = params.captchaVerification
  // 验证成功后自动提交登录
  await submitLogin()
}

// 滑块验证失败
function onVerifyError() {
  ElMessage.warning('验证失败，请重试')
}

// 提交登录请求
async function submitLogin() {
  loading.value = true
  try {
    const loginParams = {
      username: form.value.username,
      password: form.value.password,
      captchaVerification: captchaVerification.value
    }

    await userStore.loginAction(loginParams)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/home'
    router.push(redirect)
  } catch {
    captchaVerification.value = ''
    // 登录失败，刷新验证码让用户重新验证
    verifyRef.value.instance?.refresh?.()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-form-wrapper {
  animation: fadeIn 0.3s ease;
}

.login-form {
  .login-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 10px;
    transition: all 0.3s ease;
    background: var(--color-primary);
    border-color: var(--color-primary);
    box-shadow: 0 4px 14px rgba(59, 130, 246, 0.25);

    &:hover {
      background: var(--color-primary-hover);
      border-color: var(--color-primary-hover);
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(59, 130, 246, 0.35);
    }

    &:active {
      transform: translateY(0);
    }
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    padding: 4px 12px;
    transition: all 0.2s ease;
    box-shadow: 0 0 0 1px var(--color-border) inset;

    &:hover {
      box-shadow: 0 0 0 1px var(--color-primary-light) inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px var(--color-primary) inset;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 24px;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>