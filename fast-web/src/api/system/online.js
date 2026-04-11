import request from '@/utils/request'

// 获取在线用户列表
export function listOnlineUsers(params) {
  return request({
    url: '/system/online/list',
    method: 'get',
    params
  })
}

// 强制退出
export function forceLogout(tokenId) {
  return request({
    url: `/system/online/${tokenId}`,
    method: 'delete'
  })
}