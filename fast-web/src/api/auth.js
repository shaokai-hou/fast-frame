import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 手机号登录
export function loginByPhone(data) {
  return request({
    url: '/auth/loginByPhone',
    method: 'post',
    data
  })
}

// 发送登录短信验证码（需滑块验证）
export function sendLoginSmsCode(data) {
  return request({
    url: '/sms/sendLoginCode',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/auth/userInfo',
    method: 'get'
  })
}

// 获取路由信息
export function getRoutes() {
  return request({
    url: '/auth/routes',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}