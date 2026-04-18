import request from '@/utils/request'

// 定时任务 API

/**
 * 获取任务列表
 */
export function listJob(params) {
  return request({
    url: '/monitor/job/page',
    method: 'get',
    params
  })
}

/**
 * 获取任务详情
 */
export function getJob(id) {
  return request({
    url: `/monitor/job/${id}`,
    method: 'get'
  })
}

/**
 * 新增任务
 */
export function addJob(data) {
  return request({
    url: '/monitor/job',
    method: 'post',
    data
  })
}

/**
 * 修改任务
 */
export function updateJob(data) {
  return request({
    url: '/monitor/job',
    method: 'put',
    data
  })
}

/**
 * 删除任务
 */
export function deleteJob(ids) {
  return request({
    url: `/monitor/job/${ids}`,
    method: 'delete'
  })
}

/**
 * 切换任务状态
 */
export function changeJobStatus(id, status) {
  return request({
    url: '/monitor/job/changeStatus',
    method: 'put',
    params: { id, status }
  })
}

/**
 * 立即执行一次任务
 */
export function runJob(id) {
  return request({
    url: `/monitor/job/run/${id}`,
    method: 'put'
  })
}

/**
 * 校验Cron表达式
 */
export function checkCronExpression(cronExpression) {
  return request({
    url: '/monitor/job/cronExpression',
    method: 'get',
    params: { cronExpression }
  })
}

// 定时任务日志 API

/**
 * 获取日志列表
 */
export function listJobLog(params) {
  return request({
    url: '/monitor/jobLog/page',
    method: 'get',
    params
  })
}

/**
 * 获取日志详情
 */
export function getJobLog(id) {
  return request({
    url: `/monitor/jobLog/${id}`,
    method: 'get'
  })
}

/**
 * 删除日志
 */
export function deleteJobLog(ids) {
  return request({
    url: `/monitor/jobLog/${ids}`,
    method: 'delete'
  })
}

/**
 * 清空日志
 */
export function clearJobLog() {
  return request({
    url: '/monitor/jobLog/clear',
    method: 'delete'
  })
}