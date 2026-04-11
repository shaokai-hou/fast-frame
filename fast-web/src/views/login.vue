<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 标题 -->
      <div class="login-header">
        <div class="logo-wrapper">
          <img src="@/assets/logo.svg" alt="logo" class="login-logo" />
        </div>
        <h1 class="login-title">Fast Frame</h1>
        <p class="login-subtitle">后台管理系统</p>
      </div>

      <!-- 表单 -->
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
        <el-form-item prop="captcha" v-if="captchaEnabled">
          <div class="captcha-row">
            <el-input
              v-model="form.captcha"
              placeholder="请输入验证码"
              prefix-icon="Picture"
              size="large"
              @keyup.enter="handleLogin"
            />
            <img
              :src="captchaImg"
              class="captcha-img"
              @click="refreshCaptcha"
              alt="验证码"
              title="点击刷新"
            />
          </div>
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

    <!-- 底部版权 -->
    <div class="footer">
      <p>© 2024 Fast Frame. All rights reserved.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCaptcha } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const captchaEnabled = ref(true)
const captchaImg = ref('')
const form = ref({
  username: '',
  password: '',
  captcha: '',
  uuid: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// 获取验证码
async function refreshCaptcha() {
  try {
    const res = await getCaptcha()
    captchaImg.value = res.data.img
    form.value.uuid = res.data.uuid
    form.value.captcha = ''
  } catch (e) {
    // 获取验证码失败，静默处理
  }
}

// 登录
async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.loginAction(form.value)

    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/home'
    router.push(redirect)
  } catch (e) {
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--color-background);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 48px 40px;
  background: var(--color-surface);
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(15, 23, 42, 0.08);
  animation: slideUp 0.5s ease-out;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  background: var(--color-primary-lighter);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-logo {
  width: 36px;
  height: 36px;
}

.login-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-foreground);
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}

.login-subtitle {
  font-size: 14px;
  color: var(--color-foreground-muted);
  margin: 0;
}

.login-form {
  .captcha-row {
    display: flex;
    gap: 12px;

    .el-input {
      flex: 1;
    }
  }

  .captcha-img {
    width: 120px;
    height: 40px;
    cursor: pointer;
    border-radius: 8px;
    border: 1px solid var(--color-border);
    transition: all 0.2s ease;

    &:hover {
      border-color: var(--color-primary);
      transform: scale(1.02);
    }
  }

  .login-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 10px;
    transition: all 0.3s ease;
    box-shadow: 0 4px 14px rgba(37, 99, 235, 0.25);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(37, 99, 235, 0.35);
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
}

.footer {
  margin-top: 32px;
  text-align: center;

  p {
    font-size: 13px;
    color: var(--color-foreground-subtle);
    margin: 0;
  }
}

// 入场动画
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>