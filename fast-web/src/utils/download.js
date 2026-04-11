import axios from 'axios'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { saveAs } from 'file-saver'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 60000  // 下载超时时间更长
})

// 请求拦截器：添加 token
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

/**
 * 检查 blob 是否为 JSON 错误响应
 * @param {Blob} blob
 * @returns {Promise<{isError: boolean, data: any}>}
 */
async function checkBlobError(blob) {
  // 如果 blob 类型是 JSON，可能是错误响应
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
 * 下载文件
 * @param {string} url 下载地址
 * @param {string} filename 保存的文件名
 * @returns {Promise}
 */
export function download(url, filename) {
  return service({
    url,
    method: 'get',
    responseType: 'blob'
  }).then(async response => {
    const blob = response.data

    // 检查是否为错误响应（JSON 格式的 blob）
    const { isError, data } = await checkBlobError(blob)
    if (isError) {
      // 是错误响应
      const message = data?.msg || data?.message || '下载失败'
      if (data?.code === 401) {
        ElMessage.error('未授权，请重新登录')
      } else {
        ElMessage.error(message)
      }
      return Promise.reject(new Error(message))
    }

    // 正常文件，保存
    saveAs(blob, filename)
  }).catch(error => {
    // axios 抛出的错误（网络错误或非 2xx 响应）
    if (error.response) {
      const blob = error.response.data
      // 尝试解析 blob 中的错误信息
      checkBlobError(blob).then(({ isError, data }) => {
        if (isError && data) {
          if (error.response.status === 401 || data.code === 401) {
            ElMessage.error('未授权，请重新登录')
          } else {
            ElMessage.error(data.msg || data.message || '下载失败')
          }
        } else {
          // 无法解析，根据状态码显示错误
          switch (error.response.status) {
            case 401:
              ElMessage.error('未授权，请重新登录')
              break
            case 404:
              ElMessage.error('文件不存在')
              break
            case 500:
              ElMessage.error('服务器错误')
              break
            default:
              ElMessage.error('下载失败')
          }
        }
      })
    } else {
      ElMessage.error('网络错误，请检查连接')
    }
    return Promise.reject(error)
  })
}

export default service