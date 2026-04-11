import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getUserInfo, getRoutes, getProfile } from '@/api/auth'
import { setToken, removeToken, getToken, setUserInfo } from '@/utils/auth'
import router from '@/router'
import { usePermissionStore } from './permission'

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
    removeToken()
    router.push('/login')
  }

  return {
    userInfo,
    token,
    loginAction,
    getUserInfoAction,
    getRoutesAction,
    updateUserInfo,
    refreshUserInfo,
    logoutAction
  }
})