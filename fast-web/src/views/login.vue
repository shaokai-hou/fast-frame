<template>
  <div class="login-container">
    <!-- 左侧品牌区域 - Teal 渐变背景 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="brand-logo">
          <img src="@/assets/logo.svg" alt="logo" class="logo-img" />
        </div>
        <h1 class="brand-title">Fast Frame</h1>
        <p class="brand-subtitle">企业级后台管理系统</p>
        <div class="brand-features">
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>安全可靠</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>高效便捷</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>灵活扩展</span>
          </div>
        </div>
      </div>
      <!-- 背景装饰 -->
      <div class="brand-decoration">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="login-section">
      <div class="login-card">
        <!-- 标题 -->
        <div class="login-header">
          <h2 class="login-title">欢迎登录</h2>
          <p class="login-subtitle">请输入您的账号信息</p>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
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
  position: relative;
  overflow: hidden;
}

// 左侧品牌区域 - Logo Blue 渐变
.brand-section {
  width: 45%;
  min-height: 100vh;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;

  @media (max-width: 768px) {
    display: none;
  }
}

.brand-content {
  text-align: center;
  z-index: 10;
  color: var(--color-foreground-inverse);
  padding: 40px;
}

.brand-logo {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);

  .logo-img {
    width: 48px;
    height: 48px;
    filter: brightness(0) invert(1);
  }
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px 0;
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-size: 16px;
  margin: 0 0 40px 0;
  opacity: 0.9;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;

  .feature-item {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 14px;
    opacity: 0.85;

    .el-icon {
      font-size: 16px;
    }
  }
}

// 背景装饰圆圈
.brand-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;

  .circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
  }

  .circle-1 {
    width: 300px;
    height: 300px;
    top: -100px;
    left: -100px;
  }

  .circle-2 {
    width: 200px;
    height: 200px;
    bottom: -50px;
    right: -50px;
  }

  .circle-3 {
    width: 150px;
    height: 150px;
    top: 50%;
    left: 20%;
    opacity: 0.5;
  }
}

// 右侧登录区域
.login-section {
  width: 55%;
  min-height: 100vh;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 60px;

  @media (max-width: 768px) {
    width: 100%;
    padding: 20px;
  }
}

.login-card {
  width: 100%;
  max-width: 400px;
  animation: slideUp 0.5s ease-out;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 28px;
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
    border: 1px solid var(--color-border-light);
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
    box-shadow: 0 0 0 1px var(--color-border-light) inset;

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

.footer {
  margin-top: 48px;
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