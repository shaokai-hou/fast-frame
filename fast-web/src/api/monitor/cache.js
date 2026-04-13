import request from '@/utils/request'

// 缓存管理 API

/**
 * 分页查询缓存键名列表
 */
export function listCacheKeys(params) {
  return request({
    url: '/monitor/cache/list',
    method: 'get',
    params
  })
}

/**
 * 获取缓存详情
 */
export function getCacheInfo(key) {
  return request({
    url: '/monitor/cache/info',
    method: 'get',
    params: { key }
  })
}

/**
 * 删除指定缓存
 */
export function deleteCache(key) {
  return request({
    url: '/monitor/cache',
    method: 'delete',
    params: { key }
  })
}

/**
 * 清空业务缓存
 */
export function clearCache() {
  return request({
    url: '/monitor/cache/clear',
    method: 'delete'
  })
}

/**
 * 获取Redis信息
 */
export function getRedisInfo() {
  return request({
    url: '/monitor/cache/redisInfo',
    method: 'get'
  })
}