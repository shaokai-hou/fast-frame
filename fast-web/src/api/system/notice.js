import request from '@/utils/request'

// 通知公告 API

/**
 * 获取公告列表
 */
export function listNotice(params) {
  return request({
    url: '/system/notice/list',
    method: 'get',
    params
  })
}

/**
 * 获取公告详情
 */
export function getNotice(id) {
  return request({
    url: `/system/notice/${id}`,
    method: 'get'
  })
}

/**
 * 新增公告
 */
export function addNotice(data) {
  return request({
    url: '/system/notice',
    method: 'post',
    data
  })
}

/**
 * 修改公告
 */
export function updateNotice(data) {
  return request({
    url: '/system/notice',
    method: 'put',
    data
  })
}

/**
 * 删除公告
 */
export function deleteNotice(ids) {
  return request({
    url: `/system/notice/${ids}`,
    method: 'delete'
  })
}