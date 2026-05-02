<template>
  <el-dropdown
    class="user-dropdown"
    trigger="click"
    popper-class="user-dropdown-popper"
  >
    <div class="user-info">
      <el-avatar
        :size="32"
        :src="userStore.userInfo.avatar"
      >
        <el-icon><UserFilled /></el-icon>
      </el-avatar>
      <span class="username">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</span>
      <el-icon class="arrow">
        <ArrowDown />
      </el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <!-- 用户信息卡片 -->
        <div class="user-card-header">
          <el-avatar
            :size="48"
            :src="userStore.userInfo.avatar"
          >
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <div class="user-info-text">
            <div class="user-name">
              {{ userStore.userInfo.nickname || userStore.userInfo.username }}
            </div>
            <div class="user-role">
              {{ roleText }}
            </div>
          </div>
        </div>

        <el-dropdown-item
          divided
          @click="goProfile"
        >
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-dropdown-item>

        <el-dropdown-item @click="goPassword">
          <el-icon><Key /></el-icon>
          <span>修改密码</span>
        </el-dropdown-item>

        <el-dropdown-item
          divided
          @click="handleLogout"
        >
          <el-icon class="logout-icon">
            <SwitchButton />
          </el-icon>
          <span class="logout-text">退出登录</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { UserFilled, ArrowDown, User, SwitchButton, Key } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 角色文本
const roleText = computed(() => {
  const roles = userStore.userInfo.roles
  if (roles && roles.length > 0) {
    return roles.map(r => r.roleName).join('、')
  }
  return '普通用户'
})

function goProfile() {
  router.push('/profile')
}

function goPassword() {
  router.push('/profile?tab=security')
}

async function handleLogout() {
  await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  userStore.logoutAction()
}
</script>

<style scoped lang="scss">
.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;

  &:hover {
    background: var(--gray-100);
  }

  .username {
    font-size: 14px;
    font-weight: 500;
    color: var(--color-foreground);
  }

  .arrow {
    font-size: 12px;
    color: var(--color-foreground-muted);
    transition: transform 0.2s ease;
  }
}

.user-info:hover .arrow {
  transform: rotate(180deg);
}
</style>