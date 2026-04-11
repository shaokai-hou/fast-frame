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

// 记住密码相关
const REMEMBER_KEY = 'remember_password'
const USERNAME_KEY = 'remember_username'
const PASSWORD_KEY = 'remember_password_value'

export function setRememberPassword(username, password) {
  localStorage.setItem(REMEMBER_KEY, 'true')
  localStorage.setItem(USERNAME_KEY, username)
  localStorage.setItem(PASSWORD_KEY, btoa(password))
}

export function getRememberPassword() {
  const remember = localStorage.getItem(REMEMBER_KEY)
  if (remember === 'true') {
    return {
      remember: true,
      username: localStorage.getItem(USERNAME_KEY) || '',
      password: localStorage.getItem(PASSWORD_KEY) ? atob(localStorage.getItem(PASSWORD_KEY)) : ''
    }
  }
  return { remember: false, username: '', password: '' }
}

export function clearRememberPassword() {
  localStorage.removeItem(REMEMBER_KEY)
  localStorage.removeItem(USERNAME_KEY)
  localStorage.removeItem(PASSWORD_KEY)
}