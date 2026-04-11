<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <div class="error-icon">
        <el-icon :size="80"><WarningFilled /></el-icon>
      </div>
      <h2 class="error-title">页面不存在</h2>
      <p class="error-desc">抱歉，您访问的页面不存在或已被移除</p>
      <el-button type="primary" size="large" @click="goHome" class="back-btn">
        <el-icon><HomeFilled /></el-icon>
        <span>返回首页</span>
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { WarningFilled, HomeFilled } from '@element-plus/icons-vue'

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
}

.not-found-content {
  text-align: center;
  animation: slideUp 0.5s ease-out;
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  color: var(--gray-200);
  line-height: 1;
  margin-bottom: -40px;
  user-select: none;
}

.error-icon {
  color: var(--color-warning);
  margin-bottom: 24px;
}

.error-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-foreground);
  margin: 0 0 12px 0;
}

.error-desc {
  font-size: 14px;
  color: var(--color-foreground-muted);
  margin: 0 0 32px 0;
}

.back-btn {
  padding: 12px 32px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.25);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(37, 99, 235, 0.35);
  }

  .el-icon {
    margin-right: 8px;
  }
}

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