<template>
  <div class="user-card">
    <div class="avatar-wrapper">
      <el-avatar
        :size="100"
        :src="userInfo.avatar"
      >
        <el-icon :size="40">
          <UserFilled />
        </el-icon>
      </el-avatar>
      <div
        class="avatar-upload"
        @click="triggerAvatarUpload"
      >
        <el-icon><Camera /></el-icon>
      </div>
      <input
        ref="avatarInputRef"
        type="file"
        accept="image/*"
        style="display: none"
        @change="handleAvatarChange"
      >
    </div>
    <h2 class="user-name">
      {{ userInfo.nickname || userInfo.username }}
    </h2>
    <p class="user-role">
      {{ roleText }}
    </p>
    <div class="user-info-list">
      <div class="info-item">
        <el-icon><User /></el-icon>
        <span>{{ userInfo.username }}</span>
      </div>
      <div class="info-item">
        <el-icon><Phone /></el-icon>
        <span>{{ userInfo.phone || '未设置手机号' }}</span>
      </div>
      <div class="info-item">
        <el-icon><Message /></el-icon>
        <span>{{ userInfo.email || '未设置邮箱' }}</span>
      </div>
      <div class="info-item">
        <el-icon><Calendar /></el-icon>
        <span>{{ userInfo.createTime || '--' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, Camera, User, Phone, Message, Calendar } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { uploadAvatar } from '@/api/profile'

const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['avatar-updated'])

const userStore = useUserStore()

// 角色文本
const roleText = computed(() => {
  const roles = props.userInfo.roles
  if (roles && roles.length > 0) {
    return roles.map(r => r.roleName).join('、')
  }
  return '普通用户'
})

// 头像上传
const avatarInputRef = ref(null)

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

// 处理头像变更
const handleAvatarChange = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  // 检查文件类型
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return
  }

  // 检查文件大小（最大 2MB）
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return
  }

  try {
    const res = await uploadAvatar(file)
    const avatarUrl = `/api/system/file/avatar/${res.data.fileName}`
    userStore.updateUserInfo({ avatar: avatarUrl })
    emit('avatar-updated', avatarUrl)
    ElMessage.success('头像更新成功')
  } catch (e) {
    ElMessage.error('头像上传失败')
  } finally {
    event.target.value = ''
  }
}
</script>

<style scoped lang="scss">
.user-card {
  background: var(--color-surface);
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;

  :deep(.el-avatar) {
    border: 4px solid var(--color-primary-lighter);
    background: var(--color-primary-lighter);
  }

  .avatar-upload {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 32px;
    height: 32px;
    background: var(--color-primary);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
    border: 2px solid var(--color-surface);

    &:hover {
      background: var(--color-primary-hover);
      transform: scale(1.1);
    }

    .el-icon {
      color: #fff;
      font-size: 16px;
    }
  }
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-foreground);
  margin: 0 0 8px 0;
}

.user-role {
  font-size: 14px;
  color: var(--color-foreground-muted);
  margin: 0 0 24px 0;
}

.user-info-list {
  text-align: left;
  border-top: 1px solid var(--color-border-light);
  padding-top: 20px;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  color: var(--color-foreground-muted);
  font-size: 14px;

  .el-icon {
    font-size: 18px;
    color: var(--color-primary);
  }

  &:not(:last-child) {
    border-bottom: 1px solid var(--color-border-light);
  }

  &:last-child {
    flex: 1;
    align-items: flex-start;
  }
}
</style>