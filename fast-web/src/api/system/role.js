import request from '@/utils/request'

// 获取角色列表
export function listRole(params) {
  return request({
    url: '/system/role/page',
    method: 'get',
    params
  })
}

// 获取所有角色
export function listAllRole() {
  return request({
    url: '/system/role/all',
    method: 'get'
  })
}

// 获取角色详情
export function getRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(ids) {
  return request({
    url: `/system/role/${ids}`,
    method: 'delete'
  })
}

// 获取菜单树
export function getMenuTree() {
  return request({
    url: '/system/role/menuTree',
    method: 'get'
  })
}

// 获取角色菜单ID
export function getRoleMenuIds(roleId) {
  return request({
    url: `/system/role/menuIds/${roleId}`,
    method: 'get'
  })
}

// 修改状态
export function changeStatus(data) {
  return request({
    url: '/system/role/changeStatus',
    method: 'put',
    data
  })
}