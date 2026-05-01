import request from '@/utils/request'

// 获取参数配置列表
export function listConfig(params) {
  return request({
    url: '/system/config/page',
    method: 'get',
    params
  })
}

// 根据参数键名获取参数值
export function getConfigByKey(configKey) {
  return request({
    url: `/system/config/key/${configKey}`,
    method: 'get'
  })
}

// 获取参数配置详情
export function getConfig(id) {
  return request({
    url: `/system/config/${id}`,
    method: 'get'
  })
}

// 新增参数配置
export function addConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data
  })
}

// 修改参数配置
export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data
  })
}

// 删除参数配置
export function deleteConfig(ids) {
  return request({
    url: `/system/config/${ids}`,
    method: 'delete'
  })
}

// 刷新参数配置缓存
export function refreshConfigCache() {
  return request({
    url: '/system/config/cache',
    method: 'delete'
  })
}