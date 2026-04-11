const TOKEN_KEY = 'access_token'
const USER_INFO_KEY = 'user_info'

// 获取Token
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

// 设置Token
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

// 移除Token
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_INFO_KEY)
}

// 获取用户信息
export function getUserInfo() {
  const info = localStorage.getItem(USER_INFO_KEY)
  return info ? JSON.parse(info) : null
}

// 设置用户信息
export function setUserInfo(info) {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
}