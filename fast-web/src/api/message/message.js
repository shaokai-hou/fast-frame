import request from '@/utils/request'

// 消息 API

/**
 * 发送消息
 */
export function sendMessage(data) {
  return request({
    url: '/message/send',
    method: 'post',
    data
  })
}

/**
 * 获取收件箱消息列表
 */
export function listInbox(params) {
  return request({
    url: '/message/inbox',
    method: 'get',
    params
  })
}

/**
 * 获取已发送消息列表
 */
export function listSent(params) {
  return request({
    url: '/message/sent',
    method: 'get',
    params
  })
}

/**
 * 获取消息详情
 */
export function getMessageDetail(id) {
  return request({
    url: `/message/${id}`,
    method: 'get'
  })
}

/**
 * 标记消息已读
 */
export function markAsRead(id) {
  return request({
    url: `/message/read/${id}`,
    method: 'put'
  })
}

/**
 * 标记所有消息已读
 */
export function markAllAsRead() {
  return request({
    url: '/message/readAll',
    method: 'put'
  })
}

/**
 * 删除消息
 */
export function deleteMessage(ids) {
  return request({
    url: `/message/${ids}`,
    method: 'delete'
  })
}

/**
 * 获取未读消息数量
 */
export function getUnreadCount() {
  return request({
    url: '/message/unreadCount',
    method: 'get'
  })
}