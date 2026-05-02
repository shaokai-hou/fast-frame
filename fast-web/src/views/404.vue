<template>
  <div class="not-found">
    <div class="bg-decoration">
      <div class="circle circle-1" />
      <div class="circle circle-2" />
    </div>
    <div class="not-found-content">
      <div class="error-code">
        404
      </div>
      <h2 class="error-title">
        页面不存在
      </h2>
      <p class="error-desc">
        抱歉，您访问的页面不存在或已被移除
      </p>
      <div class="action-buttons">
        <el-button
          type="primary"
          size="large"
          class="action-btn"
          @click="goHome"
        >
          <el-icon><HomeFilled /></el-icon>
          <span>返回首页</span>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<style scoped lang="scss">
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-background);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;

  .circle {
    position: absolute;
    border-radius: 50%;
    opacity: 0.03;
    background: var(--color-primary);
  }

  .circle-1 {
    width: 400px;
    height: 400px;
    top: -100px;
    right: -100px;
    animation: float 8s ease-in-out infinite;
  }

  .circle-2 {
    width: 300px;
    height: 300px;
    bottom: -50px;
    left: -50px;
    animation: float 6s ease-in-out infinite reverse;
  }
}

.not-found-content {
  text-align: center;
  position: relative;
  z-index: 1;
}

.error-code {
  font-size: 160px;
  font-weight: 800;
  line-height: 1;
  margin-bottom: 16px;
  user-select: none;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: codeReveal 0.6s ease-out;
}

.error-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-foreground);
  margin: 0 0 12px 0;
  animation: textReveal 0.5s ease-out 0.2s both;
}

.error-desc {
  font-size: 15px;
  color: var(--color-foreground-muted);
  margin: 0 0 40px 0;
  animation: textReveal 0.5s ease-out 0.3s both;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  animation: buttonsReveal 0.5s ease-out 0.4s both;
}

.action-btn {
  padding: 12px 28px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.25s ease;

  .el-icon {
    margin-right: 8px;
  }

  &:hover {
    transform: translateY(-2px);
  }
}

.action-btn.secondary {
  background: var(--color-surface);
  color: var(--color-foreground);
  border: 1px solid var(--color-border);

  &:hover {
    background: var(--color-surface-hover);
    border-color: var(--color-border-hover);
  }
}

@keyframes codeReveal {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes textReveal {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes buttonsReveal {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(20px, 20px);
  }
}
</style>