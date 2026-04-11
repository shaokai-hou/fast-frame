import request from '@/utils/request'

// 获取菜单列表
export function listMenu(params) {
  return request({
    url: '/system/menu/list',
    method: 'get',
    params
  })
}

// 获取菜单详情
export function getMenu(id) {
  return request({
    url: `/system/menu/${id}`,
    method: 'get'
  })
}

// 获取菜单树
export function getMenuTree() {
  return request({
    url: '/system/menu/tree',
    method: 'get'
  })
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

// 修改菜单
export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `/system/menu/${id}`,
    method: 'delete'
  })
}