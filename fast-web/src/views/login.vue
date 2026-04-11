<template>
  <div class="login-container">
    <!-- 几何装饰背景 -->
    <div class="geo-bg">
      <!-- 渐变光晕层 -->
      <div class="glow glow-1"></div>
      <div class="glow glow-2"></div>
      <div class="glow glow-3"></div>

      <!-- 网格背景 -->
      <div class="geo-grid"></div>

      <!-- 圆形装饰 -->
      <div class="geo-shape circle circle-1"></div>
      <div class="geo-shape circle circle-2"></div>
      <div class="geo-shape circle circle-3"></div>
      <div class="geo-shape circle circle-4"></div>
      <div class="geo-shape circle circle-5"></div>

      <!-- 方形装饰 -->
      <div class="geo-shape square square-1"></div>
      <div class="geo-shape square square-2"></div>
      <div class="geo-shape square square-3"></div>
      <div class="geo-shape square square-4"></div>

      <!-- 三角形装饰 -->
      <div class="geo-shape triangle triangle-1"></div>
      <div class="geo-shape triangle triangle-2"></div>
      <div class="geo-shape triangle triangle-3"></div>

      <!-- 圆环装饰 -->
      <div class="geo-shape ring ring-1"></div>
      <div class="geo-shape ring ring-2"></div>
      <div class="geo-shape ring ring-3"></div>

      <!-- 点阵装饰 -->
      <div class="dots dots-1"></div>
      <div class="dots dots-2"></div>

      <!-- 浮动线条 -->
      <div class="floating-line line-1"></div>
      <div class="floating-line line-2"></div>
      <div class="floating-line line-3"></div>

      <!-- 十字装饰 -->
      <div class="cross cross-1"></div>
      <div class="cross cross-2"></div>
      <div class="cross cross-3"></div>
    </div>

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
          <el-checkbox v-model="form.rememberMe">记住密码</el-checkbox>
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
import { getRememberPassword, setRememberPassword, clearRememberPassword } from '@/utils/auth'

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
  uuid: '',
  rememberMe: false
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
  } catch (e) {
    console.error('获取验证码失败', e)
  }
}

// 登录
async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.loginAction(form.value)

    // 记住密码
    if (form.value.rememberMe) {
      setRememberPassword(form.value.username, form.value.password)
    } else {
      clearRememberPassword()
    }

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
  // 恢复记住的密码
  const remembered = getRememberPassword()
  if (remembered.remember) {
    form.value.username = remembered.username
    form.value.password = remembered.password
    form.value.rememberMe = true
  }
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

// 几何装饰背景
.geo-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

// 渐变光晕
.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);

  &.glow-1 {
    width: 500px;
    height: 500px;
    top: -200px;
    left: -100px;
    background: linear-gradient(135deg, var(--color-primary-lighter), transparent);
    opacity: 0.8;
    animation: glow-pulse 8s ease-in-out infinite;
  }

  &.glow-2 {
    width: 400px;
    height: 400px;
    bottom: -150px;
    right: -100px;
    background: linear-gradient(135deg, var(--color-success-lighter, rgba(16, 185, 129, 0.2)), transparent);
    opacity: 0.6;
    animation: glow-pulse 10s ease-in-out infinite reverse;
    animation-delay: -3s;
  }

  &.glow-3 {
    width: 300px;
    height: 300px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: linear-gradient(135deg, var(--color-primary-lighter), transparent);
    opacity: 0.3;
    animation: glow-pulse 12s ease-in-out infinite;
    animation-delay: -6s;
  }
}

// 网格背景
.geo-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(var(--color-border-light) 1px, transparent 1px),
    linear-gradient(90deg, var(--color-border-light) 1px, transparent 1px);
  background-size: 50px 50px;
  opacity: 0.5;
}

// 通用几何形状样式
.geo-shape {
  position: absolute;
  opacity: 0.7;
}

// 圆形装饰
.circle {
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary-lighter), transparent);

  &.circle-1 {
    width: 300px;
    height: 300px;
    top: -80px;
    left: -60px;
    animation: float 20s ease-in-out infinite;
  }

  &.circle-2 {
    width: 180px;
    height: 180px;
    bottom: 15%;
    right: 5%;
    animation: float 15s ease-in-out infinite reverse;
    animation-delay: -5s;
  }

  &.circle-3 {
    width: 100px;
    height: 100px;
    top: 35%;
    right: 20%;
    background: linear-gradient(135deg, var(--color-success-lighter, rgba(16, 185, 129, 0.2)), transparent);
    animation: float 18s ease-in-out infinite;
    animation-delay: -8s;
  }

  &.circle-4 {
    width: 60px;
    height: 60px;
    bottom: 25%;
    left: 15%;
    background: linear-gradient(135deg, var(--color-warning-lighter, rgba(245, 158, 11, 0.2)), transparent);
    animation: float 12s ease-in-out infinite;
    animation-delay: -3s;
  }

  &.circle-5 {
    width: 40px;
    height: 40px;
    top: 20%;
    left: 30%;
    animation: float 14s ease-in-out infinite reverse;
    animation-delay: -7s;
  }
}

// 方形装饰
.square {
  border-radius: 12px;
  background: var(--color-primary-lighter);
  transform: rotate(45deg);

  &.square-1 {
    width: 80px;
    height: 80px;
    top: 12%;
    left: 10%;
    animation: float 16s ease-in-out infinite, rotate-slow 30s linear infinite;
    animation-delay: -3s;
  }

  &.square-2 {
    width: 50px;
    height: 50px;
    bottom: 20%;
    left: 8%;
    background: var(--color-warning-lighter, rgba(245, 158, 11, 0.2));
    animation: float 14s ease-in-out infinite reverse, rotate-slow 25s linear infinite reverse;
    animation-delay: -7s;
  }

  &.square-3 {
    width: 35px;
    height: 35px;
    top: 60%;
    right: 12%;
    background: var(--color-success-lighter, rgba(16, 185, 129, 0.2));
    animation: float 18s ease-in-out infinite, rotate-slow 20s linear infinite;
    animation-delay: -2s;
  }

  &.square-4 {
    width: 25px;
    height: 25px;
    bottom: 40%;
    right: 8%;
    animation: float 12s ease-in-out infinite reverse, rotate-slow 22s linear infinite reverse;
    animation-delay: -5s;
  }
}

// 三角形装饰
.triangle {
  width: 0;
  height: 0;

  &.triangle-1 {
    border-left: 35px solid transparent;
    border-right: 35px solid transparent;
    border-bottom: 60px solid var(--color-primary-lighter);
    top: 25%;
    right: 8%;
    animation: float 12s ease-in-out infinite;
    animation-delay: -2s;
  }

  &.triangle-2 {
    border-left: 25px solid transparent;
    border-right: 25px solid transparent;
    border-top: 45px solid var(--color-danger-lighter, rgba(239, 68, 68, 0.15));
    bottom: 35%;
    left: 12%;
    animation: float 18s ease-in-out infinite reverse;
    animation-delay: -4s;
  }

  &.triangle-3 {
    border-left: 20px solid transparent;
    border-right: 20px solid transparent;
    border-bottom: 35px solid var(--color-warning-lighter, rgba(245, 158, 11, 0.2));
    top: 70%;
    left: 25%;
    animation: float 14s ease-in-out infinite;
    animation-delay: -6s;
  }
}

// 圆环装饰
.ring {
  border-radius: 50%;
  border: 3px solid var(--color-primary-light);

  &.ring-1 {
    width: 140px;
    height: 140px;
    top: 55%;
    left: 3%;
    animation: float 22s ease-in-out infinite, pulse-ring 4s ease-in-out infinite;
    animation-delay: -6s;
  }

  &.ring-2 {
    width: 80px;
    height: 80px;
    top: 15%;
    right: 22%;
    border-color: var(--color-success-light, rgba(16, 185, 129, 0.35));
    animation: float 17s ease-in-out infinite reverse, pulse-ring 3s ease-in-out infinite;
    animation-delay: -1s;
  }

  &.ring-3 {
    width: 50px;
    height: 50px;
    bottom: 12%;
    right: 18%;
    border-color: var(--color-warning-light, rgba(245, 158, 11, 0.3));
    animation: float 13s ease-in-out infinite, pulse-ring 3.5s ease-in-out infinite;
    animation-delay: -2s;
  }
}

// 点阵装饰
.dots {
  position: absolute;
  width: 100px;
  height: 100px;
  background-image: radial-gradient(var(--color-primary-light) 2px, transparent 2px);
  background-size: 15px 15px;

  &.dots-1 {
    top: 8%;
    right: 30%;
    animation: float 16s ease-in-out infinite;
    animation-delay: -4s;
  }

  &.dots-2 {
    bottom: 8%;
    left: 25%;
    background-image: radial-gradient(var(--color-success-light, rgba(16, 185, 129, 0.4)) 2px, transparent 2px);
    animation: float 14s ease-in-out infinite reverse;
    animation-delay: -8s;
  }
}

// 浮动线条
.floating-line {
  position: absolute;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--color-primary-light), transparent);

  &.line-1 {
    width: 120px;
    top: 45%;
    left: 5%;
    animation: float-line 10s ease-in-out infinite;
  }

  &.line-2 {
    width: 80px;
    top: 75%;
    right: 10%;
    background: linear-gradient(90deg, transparent, var(--color-success-light, rgba(16, 185, 129, 0.4)), transparent);
    animation: float-line 12s ease-in-out infinite reverse;
    animation-delay: -3s;
  }

  &.line-3 {
    width: 60px;
    top: 30%;
    right: 5%;
    animation: float-line 8s ease-in-out infinite;
    animation-delay: -5s;
  }
}

// 十字装饰
.cross {
  position: absolute;
  width: 20px;
  height: 20px;

  &::before,
  &::after {
    content: '';
    position: absolute;
    background: var(--color-primary-light);
  }

  &::before {
    width: 100%;
    height: 2px;
    top: 50%;
    transform: translateY(-50%);
  }

  &::after {
    width: 2px;
    height: 100%;
    left: 50%;
    transform: translateX(-50%);
  }

  &.cross-1 {
    top: 40%;
    left: 18%;
    animation: float 15s ease-in-out infinite;
    animation-delay: -2s;
  }

  &.cross-2 {
    top: 80%;
    right: 25%;
    animation: float 13s ease-in-out infinite reverse;
    animation-delay: -5s;
  }

  &.cross-3 {
    top: 15%;
    left: 40%;
    width: 15px;
    height: 15px;
    animation: float 17s ease-in-out infinite;
    animation-delay: -7s;

    &::before,
    &::after {
      background: var(--color-success-light, rgba(16, 185, 129, 0.4));
    }
  }
}

// 浮动动画
@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  25% {
    transform: translate(15px, -20px);
  }
  50% {
    transform: translate(-10px, 15px);
  }
  75% {
    transform: translate(20px, 10px);
  }
}

// 旋转动画
@keyframes rotate-slow {
  from {
    transform: rotate(45deg);
  }
  to {
    transform: rotate(405deg);
  }
}

// 圆环脉冲动画
@keyframes pulse-ring {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.08);
  }
}

// 光晕脉冲动画
@keyframes glow-pulse {
  0%, 100% {
    opacity: 0.6;
    transform: scale(1);
  }
  50% {
    opacity: 0.9;
    transform: scale(1.1);
  }
}

// 线条浮动动画
@keyframes float-line {
  0%, 100% {
    transform: translateX(0) scaleX(1);
    opacity: 0.6;
  }
  50% {
    transform: translateX(20px) scaleX(1.2);
    opacity: 1;
  }
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

  :deep(.el-checkbox__label) {
    color: var(--color-foreground-muted);
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