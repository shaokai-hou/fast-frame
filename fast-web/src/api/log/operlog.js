import request from '@/utils/request'

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