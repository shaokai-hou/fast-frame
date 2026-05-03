import request from '@/utils/request'

// 获取部门列表
export function listDept(params) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params
  })
}

// 获取部门树选择器
export function getDeptTree() {
  return request({
    url: '/system/dept/tree',
    method: 'get'
  })
}

// 获取部门详情
export function getDept(id) {
  return request({
    url: `/system/dept/${id}`,
    method: 'get'
  })
}

// 根据角色ID获取部门ID列表
export function getRoleDeptIds(roleId) {
  return request({
    url: `/system/dept/roleDeptIds/${roleId}`,
    method: 'get'
  })
}

// 新增部门
export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

// 修改部门
export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

// 删除部门
export function deleteDept(id) {
  return request({
    url: `/system/dept/${id}`,
    method: 'delete'
  })
}

// 修改状态
export function changeStatus(data) {
  return request({
    url: '/system/dept/changeStatus',
    method: 'put',
    data
  })
}