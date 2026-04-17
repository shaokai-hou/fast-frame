import request from '@/utils/request'

// 流程定义
export function listFlowDef(params) {
  return request({
    url: '/flow/def/list',
    method: 'get',
    params
  })
}

export function getFlowDef(id) {
  return request({
    url: `/flow/def/${id}`,
    method: 'get'
  })
}

export function publishFlowDef(id) {
  return request({
    url: `/flow/def/publish/${id}`,
    method: 'put'
  })
}

export function unpublishFlowDef(id) {
  return request({
    url: `/flow/def/unpublish/${id}`,
    method: 'put'
  })
}

export function deleteFlowDef(id) {
  return request({
    url: `/flow/def/${id}`,
    method: 'delete'
  })
}

// 流程实例
export function listFlowInstance(params) {
  return request({
    url: '/flow/instance/list',
    method: 'get',
    params
  })
}

export function getFlowInstance(id) {
  return request({
    url: `/flow/instance/${id}`,
    method: 'get'
  })
}

export function startFlowInstance(data) {
  return request({
    url: '/flow/instance/start',
    method: 'post',
    data
  })
}

export function terminateFlowInstance(id) {
  return request({
    url: `/flow/instance/${id}`,
    method: 'delete'
  })
}

export function getFlowDiagram(instanceId) {
  return request({
    url: `/flow/instance/diagram/${instanceId}`,
    method: 'get'
  })
}

// 任务管理
export function listTodoTask(params) {
  return request({
    url: '/flow/task/todo',
    method: 'get',
    params
  })
}

export function listDoneTask(params) {
  return request({
    url: '/flow/task/done',
    method: 'get',
    params
  })
}

export function getFlowTask(id) {
  return request({
    url: `/flow/task/${id}`,
    method: 'get'
  })
}

export function approveTask(data) {
  return request({
    url: '/flow/task/approve',
    method: 'post',
    data
  })
}

export function rejectTask(data) {
  return request({
    url: '/flow/task/reject',
    method: 'post',
    data
  })
}

export function backTask(data) {
  return request({
    url: '/flow/task/back',
    method: 'post',
    data
  })
}

export function delegateTask(data) {
  return request({
    url: '/flow/task/delegate',
    method: 'post',
    data
  })
}

export function getApprovalHistory(instanceId) {
  return request({
    url: `/flow/instance/history/${instanceId}`,
    method: 'get'
  })
}