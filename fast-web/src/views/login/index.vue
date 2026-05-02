<template>
  <div class="login-container">
    <!-- 左侧品牌区域 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="brand-logo">
          <img
            src="@/assets/logo.svg"
            alt="logo"
            class="logo-img"
          >
        </div>
        <h1 class="brand-title">
          Fast Frame
        </h1>
        <p class="brand-subtitle">
          企业级后台管理系统
        </p>
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
        <div class="circle circle-1" />
        <div class="circle circle-2" />
        <div class="circle circle-3" />
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="login-section">
      <div class="login-card">
        <h2 class="login-title">
          欢迎登录
        </h2>
        <!-- 登录方式切换 -->
        <div class="login-tabs">
          <span
            :class="['tab-item', { active: loginType === 'username' }]"
            @click="loginType = 'username'"
          >
            账号登录
          </span>
          <span class="tab-divider">|</span>
          <span
            :class="['tab-item', { active: loginType === 'phone' }]"
            @click="loginType = 'phone'"
          >
            手机号登录
          </span>
        </div>
        <!-- 表单内容 -->
        <div class="login-content">
          <LoginForm v-if="loginType === 'username'" />
          <PhoneLoginForm v-else />
        </div>
      </div>
      <!-- 底部版权 -->
      <div class="footer">
        <p>© 2024 Fast Frame. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Check } from '@element-plus/icons-vue'
import LoginForm from './components/LoginForm.vue'
import PhoneLoginForm from './components/PhoneLoginForm.vue'

const loginType = ref('username')
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  min-height: 100vh;
  background: #F5F7FA;
}

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
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-foreground);
  margin: 0 0 24px 0;
  text-align: center;
  letter-spacing: -0.02em;
}

.login-tabs {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 32px;

  .tab-item {
    font-size: 15px;
    color: var(--color-foreground-muted);
    cursor: pointer;
    transition: color 0.2s ease;

    &:hover {
      color: var(--color-primary);
    }

    &.active {
      color: var(--color-primary);
      font-weight: 600;
    }
  }

  .tab-divider {
    margin: 0 16px;
    color: var(--color-border);
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
</style>