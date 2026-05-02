<template>
  <div class="login-form-wrapper">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      class="login-form"
    >
      <el-form-item prop="phone">
        <el-input
          v-model="form.phone"
          placeholder="请输入手机号"
          prefix-icon="Iphone"
          size="large"
          maxlength="11"
        />
      </el-form-item>
      <el-form-item prop="smsCode">
        <div class="sms-input-group">
          <el-input
            v-model="form.smsCode"
            placeholder="请输入验证码"
            prefix-icon="Message"
            size="large"
            maxlength="6"
          />
          <el-button
            type="primary"
            :disabled="sending || countdown > 0"
            :loading="sending"
            class="sms-btn"
            @click="handleSendCode"
          >
            {{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
          </el-button>
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
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { sendLoginSmsCode } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const sending = ref(false)
const countdown = ref(0)

const form = ref({
  phone: '',
  smsCode: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  smsCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

let countdownTimer = null

// 点击获取验证码按钮
async function handleSendCode() {
  // 先验证手机号格式
  try {
    await formRef.value.validateField('phone')
  } catch {
    return
  }

  sending.value = true
  try {
    await sendLoginSmsCode({ phone: form.value.phone })
    ElMessage.success('验证码已发送')
    // 开始倒计时
    countdown.value = 60
    countdownTimer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(countdownTimer)
      }
    }, 1000)
  } finally {
    sending.value = false
  }
}

// 点击登录按钮
async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const loginParams = {
      phone: form.value.phone,
      smsCode: form.value.smsCode
    }

    await userStore.loginByPhoneAction(loginParams)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/home'
    router.push(redirect)
  } catch {
    // 登录失败
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
  .sms-input-group {
    display: flex;
    width: 100%;
    align-items: center;

    .el-input {
      flex: 1;
      min-width: 0;
    }

    .sms-btn {
      width: 90px;
      height: 38px;
      font-size: 13px;
      border-radius: 8px;
      margin-left: 8px;
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