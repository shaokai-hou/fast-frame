<template>
  <PageContainer :padding="false">
    <!-- 欢迎区域 -->
    <div class="welcome-section fade-in">
      <div class="welcome-text">
        <h1 class="welcome-title">
          {{ greeting }}，{{ userStore.userInfo.nickname || userStore.userInfo.username }}
        </h1>
        <p class="welcome-subtitle">
          欢迎使用 Fast Frame 后台管理系统
        </p>
      </div>
      <div class="welcome-date">
        <span class="date-text">{{ currentDate }}</span>
        <span class="week-text">{{ currentWeek }}</span>
      </div>
    </div>

    <!-- 快捷入口 -->
    <el-card
      class="quick-links-card fade-in"
      style="animation-delay: 0.2s"
    >
      <template #header>
        <span class="card-title">快捷入口</span>
      </template>
      <div
        v-if="loading"
        class="loading-state"
      >
        <el-skeleton
          :rows="2"
          animated
        />
      </div>
      <div
        v-else-if="quickLinks.length === 0"
        class="empty-state"
      >
        <el-empty
          description="暂无快捷入口"
          :image-size="80"
        />
      </div>
      <div
        v-else
        class="quick-links-grid"
      >
        <div
          v-for="(link, index) in quickLinks"
          :key="link.id"
          class="quick-link-item"
          :style="{ animationDelay: index * 0.05 + 's' }"
          @click="handleClick(link)"
        >
          <div
            class="quick-link-icon"
            :style="{ background: getGradient(index) }"
          >
            <el-icon :size="24">
              <component :is="link.icon || 'Document'" />
            </el-icon>
          </div>
          <span class="quick-link-text">{{ link.name }}</span>
        </div>
      </div>
    </el-card>
  </PageContainer>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getHomeData } from '@/api/home'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const quickLinks = ref([])

// 图标渐变色列表 - Logo Blue 为主色
const gradients = [
  'linear-gradient(135deg, #3B82F6 0%, #60A5FA 100%)',
  'linear-gradient(135deg, #2563EB 0%, #3B82F6 100%)',
  'linear-gradient(135deg, #059669 0%, #10B981 100%)',
  'linear-gradient(135deg, #D97706 0%, #FBBF24 100%)',
  'linear-gradient(135deg, #7C3AED 0%, #A78BFA 100%)',
  'linear-gradient(135deg, #DC2626 0%, #F87171 100%)'
]

// 获取渐变色
const getGradient = (index) => {
  return gradients[index % gradients.length]
}

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

// 点击快捷入口
const handleClick = (link) => {
  router.push(link.path)
}

// 获取首页数据
onMounted(async () => {
  try {
    const res = await getHomeData()
    quickLinks.value = res.data.quickLinks || []
  } catch (error) {
    console.error('获取首页数据失败:', error)
    quickLinks.value = []
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
// 欢迎区域 - Logo Blue 渐变
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--gradient-primary);
  padding: 32px 40px;
  border-radius: 16px;
  margin-bottom: 24px;
  color: var(--color-foreground-inverse);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.25);
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

// 快捷入口卡片
.quick-links-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.04);

  :deep(.el-card__header) {
    padding: 20px 24px;
    border-bottom: 1px solid var(--color-border-light);
  }

  :deep(.el-card__body) {
    padding: 24px;
  }
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-foreground);
}

// 快捷入口网格
.quick-links-grid {
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
  animation: fadeIn 0.3s ease-out forwards;
  opacity: 0;

  &:hover {
    background: var(--color-surface);
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(59, 130, 246, 0.08);
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

// 加载状态
.loading-state {
  padding: 16px;
}

// 空状态
.empty-state {
  padding: 24px;
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
    padding: 24px;
  }

  .welcome-date {
    text-align: center;
  }

  .quick-links-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .quick-links-grid {
    grid-template-columns: repeat(1, 1fr);
  }
}
</style>