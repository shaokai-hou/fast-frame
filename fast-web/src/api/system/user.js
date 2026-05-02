import request from '@/utils/request'

// 获取用户列表（简化信息，用于选择器）
export function listAllUser() {
  return request({
    url: '/system/user/list',
    method: 'get'
  })
}

// 获取用户分页列表
export function listUser(params) {
  return request({
    url: '/system/user/page',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(ids) {
  return request({
    url: `/system/user/${ids}`,
    method: 'delete'
  })
}

// 重置密码（重置为系统初始化密码）
export function resetPwd(userId) {
  return request({
    url: `/system/user/resetPwd/${userId}`,
    method: 'put'
  })
}

// 修改状态
export function changeStatus(data) {
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data
  })
}

// 导出用户
export function exportUser(params) {
  return request({
    url: '/system/user/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 导入用户
export function importUser(formData) {
  return request({
    url: '/system/user/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 下载导入模板
export function downloadTemplate() {
  return request({
    url: '/system/user/template',
    method: 'get',
    responseType: 'blob'
  })
}

// 解锁用户
export function unlockUser(userId) {
  return request({
    url: `/system/user/unlock/${userId}`,
    method: 'put'
  })
}