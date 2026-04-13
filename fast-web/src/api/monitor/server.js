import request from '@/utils/request'

// 服务监控 API

/**
 * 获取服务器信息
 */
export function getServerInfo() {
  return request({
    url: '/monitor/server',
    method: 'get'
  })
}