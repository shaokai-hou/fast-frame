import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getUserInfo, getRoutes } from '@/api/auth'
import { getProfile } from '@/api/profile'
import { setToken, removeToken, getToken, setUserInfo } from '@/utils/auth'
import router, { resetRouter } from '@/router'
import { usePermissionStore } from './permission'
import { useTagsViewStore } from './tagsView'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref({})
  // Token
  const token = ref(getToken() || '')

  // 登录
  async function loginAction(loginForm) {
    const res = await login(loginForm)
    token.value = res.data.accessToken
    setToken(res.data.accessToken)
    return res
  }

  // 获取用户信息
  async function getUserInfoAction() {
    const res = await getUserInfo()
    userInfo.value = res.data
    setUserInfo(res.data)

    // 设置权限
    const permissionStore = usePermissionStore()
    permissionStore.setPermissions(res.data.permissions || [])

    return res
  }

  // 获取路由
  async function getRoutesAction() {
    const res = await getRoutes()
    return res.data.routes || []
  }

  // 更新用户信息（本地更新）
  function updateUserInfo(newInfo) {
    userInfo.value = { ...userInfo.value, ...newInfo }
    setUserInfo(userInfo.value)
  }

  // 刷新用户信息（从服务器获取）
  async function refreshUserInfo() {
    const res = await getProfile()
    userInfo.value = res.data
    setUserInfo(res.data)
    return res
  }

  // 退出登录
  async function logoutAction() {
    await logout()
    token.value = ''
    userInfo.value = {}

    // 清除权限和路由状态
    const permissionStore = usePermissionStore()
    permissionStore.reset()

    // 清除 TagsView 状态
    const tagsViewStore = useTagsViewStore()
    tagsViewStore.resetTagsView()

    // 清除动态路由
    resetRouter()

    removeToken()
    router.push('/login')
  }

  // 仅重置 token（不调用 logout API，用于 token 失效时快速清除）
  function resetToken() {
    token.value = ''
    userInfo.value = ''
    const permissionStore = usePermissionStore()
    permissionStore.reset()
    const tagsViewStore = useTagsViewStore()
    tagsViewStore.resetTagsView()
    resetRouter()
    removeToken()
  }

  return {
    userInfo,
    token,
    loginAction,
    getUserInfoAction,
    getRoutesAction,
    updateUserInfo,
    refreshUserInfo,
    logoutAction,
    resetToken
  }
})