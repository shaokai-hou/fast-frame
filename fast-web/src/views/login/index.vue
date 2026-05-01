<template>
  <div class="login-container">
    <!-- 左侧品牌区域 -->
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

    <!-- 右侧登录表单 -->
    <LoginForm />
  </div>
</template>

<script setup>
import { Check } from '@element-plus/icons-vue'
import LoginForm from './components/LoginForm.vue'
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
</style>