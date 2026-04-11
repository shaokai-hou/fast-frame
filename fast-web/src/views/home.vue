<template>
  <div class="home-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section fade-in">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1 class="welcome-title">{{ greeting }}，{{ userStore.userInfo.nickname || userStore.userInfo.username }}</h1>
          <p class="welcome-subtitle">欢迎使用 Fast Frame 后台管理系统</p>
        </div>
        <div class="welcome-stats">
          <div class="stat-item">
            <span class="stat-number">{{ stats.userCount }}</span>
            <span class="stat-label">用户数</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.menuCount }}</span>
            <span class="stat-label">菜单数</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.onlineCount }}</span>
            <span class="stat-label">在线用户</span>
          </div>
        </div>
      </div>
      <div class="welcome-date">
        <span class="date-text">{{ currentDate }}</span>
        <span class="week-text">{{ currentWeek }}</span>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="(stat, index) in statsConfig" :key="stat.key">
        <el-card class="stat-card fade-in" :style="{ animationDelay: (index + 1) * 0.1 + 's' }">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.gradient }">
              <el-icon :size="28"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats[stat.key] }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口和系统信息 -->
    <el-row :gutter="24" class="main-row">
      <el-col :xs="24" :md="16">
        <el-card class="quick-links-card fade-in" style="animation-delay: 0.4s">
          <template #header>
            <div class="card-header">
              <span class="card-title">快捷入口</span>
            </div>
          </template>
          <div class="quick-links">
            <div
              v-for="(link, index) in quickLinks"
              :key="link.path"
              class="quick-link-item"
              @click="$router.push(link.path)"
            >
              <div class="quick-link-icon" :style="{ background: link.gradient }">
                <el-icon :size="24"><component :is="link.icon" /></el-icon>
              </div>
              <span class="quick-link-text">{{ link.name }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card class="system-info-card fade-in" style="animation-delay: 0.5s">
          <template #header>
            <div class="card-header">
              <span class="card-title">系统信息</span>
            </div>
          </template>
          <div class="system-info">
            <div class="info-item" v-for="info in systemInfo" :key="info.label">
              <span class="info-label">{{ info.label }}</span>
              <span class="info-value">{{ info.value }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const stats = ref({
  userCount: 0,
  menuCount: 0,
  roleCount: 0,
  onlineCount: 0
})

const statsConfig = [
  { key: 'userCount', label: '用户总数', icon: 'User', gradient: 'linear-gradient(135deg, #2563EB 0%, #3B82F6 100%)' },
  { key: 'menuCount', label: '菜单总数', icon: 'Document', gradient: 'linear-gradient(135deg, #7C3AED 0%, #A78BFA 100%)' },
  { key: 'roleCount', label: '角色总数', icon: 'Avatar', gradient: 'linear-gradient(135deg, #059669 0%, #10B981 100%)' },
  { key: 'onlineCount', label: '在线用户', icon: 'Connection', gradient: 'linear-gradient(135deg, #D97706 0%, #FBBF24 100%)' }
]

const quickLinks = [
  { name: '用户管理', path: '/system/user', icon: 'User', gradient: 'linear-gradient(135deg, #2563EB 0%, #3B82F6 100%)' },
  { name: '角色管理', path: '/system/role', icon: 'Avatar', gradient: 'linear-gradient(135deg, #7C3AED 0%, #A78BFA 100%)' },
  { name: '菜单管理', path: '/system/menu', icon: 'Menu', gradient: 'linear-gradient(135deg, #059669 0%, #10B981 100%)' },
  { name: '字典管理', path: '/system/dict', icon: 'Collection', gradient: 'linear-gradient(135deg, #D97706 0%, #FBBF24 100%)' },
  { name: '登录日志', path: '/log/loginlog', icon: 'Document', gradient: 'linear-gradient(135deg, #DC2626 0%, #F87171 100%)' },
  { name: '操作日志', path: '/log/operlog', icon: 'DocumentCopy', gradient: 'linear-gradient(135deg, #0891B2 0%, #22D3EE 100%)' }
]

const systemInfo = [
  { label: '系统名称', value: 'Fast Frame' },
  { label: '系统版本', value: '1.0.0' },
  { label: '后端框架', value: 'Spring Boot 2.7.x' },
  { label: '前端框架', value: 'Vue 3 + Element Plus' }
]

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
})

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
})

// 当前星期
const currentWeek = computed(() => {
  const weeks = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return weeks[new Date().getDay()]
})

onMounted(() => {
  // 模拟数据，实际应从后端获取
  stats.value = {
    userCount: 1,
    menuCount: 39,
    roleCount: 1,
    onlineCount: 1
  }
})
</script>

<style scoped lang="scss">
.home-container {
  padding: 0;
}

// 欢迎区域
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, #3B82F6 100%);
  padding: 32px;
  border-radius: 16px;
  margin-bottom: 24px;
  color: #fff;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.25);
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 48px;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}

.welcome-subtitle {
  font-size: 14px;
  margin: 0;
  opacity: 0.9;
}

.welcome-stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;

  .stat-number {
    font-size: 28px;
    font-weight: 700;
    line-height: 1.2;
  }

  .stat-label {
    font-size: 12px;
    opacity: 0.8;
    margin-top: 4px;
  }
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.3);
}

.welcome-date {
  text-align: right;

  .date-text {
    display: block;
    font-size: 16px;
    font-weight: 600;
  }

  .week-text {
    display: block;
    font-size: 13px;
    opacity: 0.8;
    margin-top: 4px;
  }
}

// 统计卡片
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  transition: all 0.3s ease;
  cursor: default;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
  }

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-foreground);
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: var(--color-foreground-muted);
  margin-top: 4px;
}

// 快捷入口
.main-row {
  margin-bottom: 0;
}

.quick-links-card,
.system-info-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-foreground);
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.quick-link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  border-radius: 14px;
  background: var(--gray-50);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: var(--color-surface);
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(15, 23, 42, 0.08);
  }
}

.quick-link-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 12px;
  transition: transform 0.3s ease;
}

.quick-link-item:hover .quick-link-icon {
  transform: scale(1.1);
}

.quick-link-text {
  font-size: 13px;
  color: var(--color-foreground);
  font-weight: 500;
}

// 系统信息
.system-info {
  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid var(--color-border-light);

    &:last-child {
      border-bottom: none;
    }
  }

  .info-label {
    color: var(--color-foreground-muted);
    font-size: 13px;
  }

  .info-value {
    color: var(--color-foreground);
    font-weight: 500;
    font-size: 13px;
  }
}

// 入场动画
.fade-in {
  animation: fadeIn 0.4s ease-out forwards;
  opacity: 0;
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

// 响应式
@media (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .welcome-content {
    flex-direction: column;
    gap: 20px;
  }

  .welcome-stats {
    flex-wrap: wrap;
    justify-content: center;
  }

  .quick-links {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>