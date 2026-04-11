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

// 获取操作日志列表
export function listOperLog(params) {
  return request({
    url: '/system/operlog/list',
    method: 'get',
    params
  })
}

// 获取操作日志详情
export function getOperLog(id) {
  return request({
    url: `/system/operlog/${id}`,
    method: 'get'
  })
}

// 删除操作日志
export function deleteOperLog(ids) {
  return request({
    url: `/system/operlog/${ids}`,
    method: 'delete'
  })
}

// 清空操作日志
export function clearOperLog() {
  return request({
    url: '/system/operlog/clear',
    method: 'delete'
  })
}