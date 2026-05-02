<template>
  <PageContainer>
    <!-- 顶部返回按钮 -->
    <div class="back-bar">
      <div
        class="back-button"
        @click="goBack"
      >
        <el-icon><ArrowLeft /></el-icon>
        <span class="back-text">返回</span>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="profile-content">
      <!-- 左侧：用户信息卡片 -->
      <div class="profile-aside">
        <ProfileCard
          :user-info="userStore.userInfo"
          @avatar-updated="handleAvatarUpdated"
        />
      </div>

      <!-- 右侧：内容区域 -->
      <div class="profile-main">
        <el-tabs
          v-model="activeTab"
          class="profile-tabs"
        >
          <!-- 基本信息 -->
          <el-tab-pane
            label="基本信息"
            name="basic"
          >
            <div class="tab-content">
              <ProfileForm
                :initial-data="profileData"
                @updated="handleProfileUpdated"
              />
            </div>
          </el-tab-pane>

          <!-- 安全设置 -->
          <el-tab-pane
            label="安全设置"
            name="security"
          >
            <div class="tab-content">
              <SecurityForm @password-changed="handlePasswordChanged" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getProfile } from '@/api/profile'
import ProfileCard from './components/ProfileCard.vue'
import ProfileForm from './components/ProfileForm.vue'
import SecurityForm from './components/SecurityForm.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

function goBack() {
  router.back()
}

const activeTab = ref('basic')
const profileData = ref(null)

const initProfile = async () => {
  try {
    const res = await getProfile()
    profileData.value = res.data
  } catch {
    // 获取个人信息失败，静默处理
  }
}

const handleAvatarUpdated = (avatarUrl) => {
  profileData.value = { ...profileData.value, avatar: avatarUrl }
}

const handleProfileUpdated = (data) => {
  profileData.value = { ...profileData.value, ...data }
}

const handlePasswordChanged = () => {
  // 密码修改后会在 SecurityForm 中自动退出登录
}

onMounted(() => {
  initProfile()
  if (route.query.tab === 'security') {
    activeTab.value = 'security'
  }
})
</script>

<style scoped lang="scss">
.back-bar {
  display: flex;
  align-items: center;
  background: var(--color-surface);
  border-radius: 12px;
  border: 1px solid var(--color-border-light);
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  padding: 12px 16px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.2s ease;

  .el-icon {
    font-size: 18px;
    color: var(--color-foreground-muted);
    transition: color 0.2s ease;
  }

  .back-text {
    color: var(--color-foreground-muted);
    transition: all 0.2s ease;
    padding: 4px 8px;
    border-radius: 6px;

    &:hover {
      background: var(--color-primary-lighter);
      color: var(--color-primary);
    }
  }

  &:hover .el-icon {
    color: var(--color-primary);
  }
}

.profile-content {
  display: flex;
  gap: 24px;
  margin-top: 16px;
  height: calc(100vh - 60px - 48px - 24px - 24px - 16px - 44px);
}

.profile-aside {
  width: 320px;
  flex-shrink: 0;
}

.profile-main {
  flex: 1;
  min-width: 0;
  height: 100%;
}

.profile-tabs {
  background: var(--color-surface);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
  height: 100%;
  display: flex;
  flex-direction: column;

  :deep(.el-tabs__header) {
    margin-bottom: 24px;
  }

  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__item) {
    font-size: 15px;
    font-weight: 500;
    color: var(--color-foreground-muted);
    padding: 0 24px;

    &.is-active {
      color: var(--color-primary);
    }

    &:hover {
      color: var(--color-primary);
    }
  }

  :deep(.el-tabs__active-bar) {
    height: 3px;
    border-radius: 2px;
  }

  :deep(.el-tabs__content) {
    flex: 1;
    overflow-y: auto;
  }

  :deep(.el-tab-pane) {
    height: 100%;
  }
}

.tab-content {
  max-width: 600px;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    height: auto;
  }

  .profile-aside {
    width: 100%;
  }

  .profile-tabs {
    height: auto;
  }
}
</style>