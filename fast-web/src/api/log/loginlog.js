import request from '@/utils/request'

// 获取登录日志列表
export function listLoginLog(params) {
  return request({
    url: '/system/loginlog/list',
    method: 'get',
    params
  })
}

// 删除登录日志
export function deleteLoginLog(ids) {
  return request({
    url: `/system/loginlog/${ids}`,
    method: 'delete'
  })
}

// 清空登录日志
export function clearLoginLog() {
  return request({
    url: '/system/loginlog/clear',
    method: 'delete'
  })
}