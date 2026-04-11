import request from '@/utils/request'

// 获取字典类型列表
export function listDictType(params) {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params
  })
}

// 根据字典类型获取字典数据
export function getDictData(dictType) {
  return request({
    url: `/system/dict/data/${dictType}`,
    method: 'get'
  })
}

// 分页查询字典数据
export function listDictData(params) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params
  })
}

// 新增字典类型
export function addDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data
  })
}

// 修改字典类型
export function updateDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data
  })
}

// 删除字典类型
export function deleteDictType(ids) {
  return request({
    url: `/system/dict/type/${ids}`,
    method: 'delete'
  })
}

// 新增字典数据
export function addDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data
  })
}

// 修改字典数据
export function updateDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data
  })
}

// 删除字典数据
export function deleteDictData(ids) {
  return request({
    url: `/system/dict/data/${ids}`,
    method: 'delete'
  })
}