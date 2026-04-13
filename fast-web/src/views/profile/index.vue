<template>
  <div class="profile-container">
    <!-- 左侧：用户信息卡片 -->
    <div class="profile-aside">
      <!-- 返回按钮 -->
      <div class="back-button" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </div>

      <div class="user-card">
        <div class="avatar-wrapper">
          <el-avatar :size="100" :src="userStore.userInfo.avatar">
            <el-icon :size="40"><UserFilled /></el-icon>
          </el-avatar>
          <div class="avatar-upload" @click="triggerAvatarUpload">
            <el-icon><Camera /></el-icon>
          </div>
          <input
            ref="avatarInputRef"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleAvatarChange"
          />
        </div>
        <h2 class="user-name">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</h2>
        <p class="user-role">{{ roleText }}</p>
        <div class="user-info-list">
          <div class="info-item">
            <el-icon><User /></el-icon>
            <span>{{ userStore.userInfo.username }}</span>
          </div>
          <div class="info-item">
            <el-icon><Phone /></el-icon>
            <span>{{ userStore.userInfo.phone || '未设置手机号' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Message /></el-icon>
            <span>{{ userStore.userInfo.email || '未设置邮箱' }}</span>
          </div>
          <div class="info-item">
            <el-icon><Calendar /></el-icon>
            <span>{{ userStore.userInfo.createTime || '--' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧：内容区域 -->
    <div class="profile-main">
      <el-tabs v-model="activeTab" class="profile-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="tab-content">
            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="80px"
              class="profile-form"
            >
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-select v-model="profileForm.gender" placeholder="请选择性别">
                  <el-option label="未知" value="0" />
                  <el-option label="男" value="1" />
                  <el-option label="女" value="2" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="profileLoading" @click="handleUpdateProfile">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 安全设置 -->
        <el-tab-pane label="安全设置" name="security">
          <div class="tab-content">
            <div class="security-section">
              <h3 class="section-title">修改密码</h3>
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
                class="password-form"
              >
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入当前密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">
                    修改密码
                  </el-button>
                  <el-button @click="resetPasswordForm">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Camera, User, Phone, Message, Calendar, ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getProfile, updateProfile, uploadAvatar, updatePassword } from '@/api/profile'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 返回上一页
function goBack() {
  router.back()
}

// 当前选项卡
const activeTab = ref('basic')

// 角色文本
const roleText = computed(() => {
  const roles = userStore.userInfo.roles
  if (roles && roles.length > 0) {
    return roles.map(r => r.roleName).join('、')
  }
  return '普通用户'
})

// 头像上传
const avatarInputRef = ref(null)
const profileLoading = ref(false)
const passwordLoading = ref(false)

// 个人资料表单
const profileFormRef = ref(null)
const profileForm = reactive({
  nickname: '',
  phone: '',
  email: '',
  gender: '0'
})

const profileRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }]
}

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 初始化个人资料
const initProfile = async () => {
  try {
    const res = await getProfile()
    const data = res.data
    profileForm.nickname = data.nickname || ''
    profileForm.phone = data.phone || ''
    profileForm.email = data.email || ''
    profileForm.gender = data.gender || '0'
  } catch (e) {
    // 获取个人信息失败，静默处理
  }
}

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
    // uploadAvatar 直接接收 file 对象
    const res = await uploadAvatar(file)
    // 返回格式: { fileName: '2026/04/11/abc.jpg' }
    // URL格式: /api/system/file/avatar/2026/04/11/abc.jpg
    const avatarUrl = `/api/system/file/avatar/${res.data.fileName}`
    userStore.updateUserInfo({ avatar: avatarUrl })
    ElMessage.success('头像更新成功')
  } catch (e) {
    ElMessage.error('头像上传失败')
  } finally {
    // 清空 input，允许重复选择同一文件
    event.target.value = ''
  }
}

// 更新个人资料
const handleUpdateProfile = async () => {
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return

  profileLoading.value = true
  try {
    await updateProfile({
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      email: profileForm.email,
      gender: profileForm.gender
    })
    userStore.updateUserInfo({
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      email: profileForm.email,
      gender: profileForm.gender
    })
    ElMessage.success('修改成功')
  } catch (e) {
    // 错误由请求拦截器处理
  } finally {
    profileLoading.value = false
  }
}

// 更新密码
const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return

  passwordLoading.value = true
  try {
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    // 清空表单
    resetPasswordForm()
    // 延迟后退出登录
    setTimeout(() => {
      userStore.logoutAction()
    }, 1500)
  } catch (e) {
    // 错误由请求拦截器处理
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.resetFields()
}

onMounted(() => {
  initProfile()
  // 根据 URL 参数设置 activeTab
  if (route.query.tab === 'security') {
    activeTab.value = 'security'
  }
})
</script>

<style scoped lang="scss">
.profile-container {
  display: flex;
  gap: 24px;
  height: calc(100vh - 60px - 48px);
  padding: 24px;
}

.profile-aside {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 12px;
  background: var(--color-surface);
  cursor: pointer;
  color: var(--color-foreground-muted);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: 1px solid var(--color-border-light);
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);

  &:hover {
    background: var(--color-primary-lighter);
    color: var(--color-primary);
    border-color: var(--color-primary-light);
  }

  .el-icon {
    font-size: 18px;
  }
}

.user-card {
  background: var(--color-surface);
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
  flex: 1;
  min-height: 0;
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

.profile-main {
  flex: 1;
  min-width: 0;
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

.profile-form {
  :deep(.el-form-item__label) {
    color: var(--color-foreground);
  }

  :deep(.el-input),
  :deep(.el-select) {
    width: 100%;
  }
}

.security-section {
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-foreground);
    margin: 0 0 24px 0;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--color-border-light);
  }
}

.password-form {
  :deep(.el-form-item__label) {
    color: var(--color-foreground);
  }

  :deep(.el-input) {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
    padding: 16px;
    height: auto;
  }

  .profile-aside {
    width: 100%;
    flex-direction: column;
  }

  .user-card {
    flex: none;
  }

  .profile-tabs {
    height: auto;
  }
}
</style>