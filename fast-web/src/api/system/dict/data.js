import request from '@/utils/request'

// 分页查询字典数据
export function listDictData(params) {
  return request({
    url: '/system/dict/data/page',
    method: 'get',
    params
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