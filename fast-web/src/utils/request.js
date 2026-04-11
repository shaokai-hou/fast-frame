import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken, removeToken } from '@/utils/auth'
import router from '@/router'
import { saveAs } from 'file-saver'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['sa-token'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 如果是 blob 类型，直接返回
    if (response.config.responseType === 'blob') {
      return response.data
    }
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      // 401 未授权
      if (res.code === 401) {
        ElMessageBox.confirm('登录状态已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          removeToken()
          router.push('/login')
        })
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    let message = '请求失败'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未授权，请重新登录'
          removeToken()
          router.push('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data?.msg || '请求失败'
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时'
    } else if (error.message.includes('Network Error')) {
      message = '网络错误'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service

/**
 * 检查 blob 是否为 JSON 错误响应
 * @param {Blob} blob
 * @returns {Promise<{isError: boolean, data: any}>}
 */
async function checkBlobError(blob) {
  if (blob.type && blob.type.includes('application/json')) {
    try {
      const text = await blob.text()
      const data = JSON.parse(text)
      return { isError: true, data }
    } catch {
      // 解析失败，不是 JSON
    }
  }
  return { isError: false, data: null }
}

/**
 * 获取文件 Blob（带认证和错误处理）
 * @param {string} url 请求地址
 * @returns {Promise<Blob>}
 */
export async function getBlob(url) {
  const response = await service({
    url,
    method: 'get',
    responseType: 'blob'
  })
  // response 已经被拦截器处理，如果是 blob 类型直接返回 response.data
  // 但拦截器返回的是 response.data，所以这里 response 就是 blob
  // 需要检查是否为错误响应
  const blob = response
  const { isError, data } = await checkBlobError(blob)
  if (isError) {
    const message = data?.msg || data?.message || '获取文件失败'
    if (data?.code === 401) {
      ElMessage.error('未授权，请重新登录')
    } else {
      ElMessage.error(message)
    }
    throw new Error(message)
  }
  return blob
}

/**
 * 下载文件（带认证和错误处理）
 * @param {string} url 下载地址
 * @param {string} filename 保存的文件名
 * @returns {Promise}
 */
export async function download(url, filename) {
  try {
    const blob = await getBlob(url)
    saveAs(blob, filename)
  } catch (error) {
    // getBlob 已经处理了错误消息，这里只需要继续抛出
    throw error
  }
}