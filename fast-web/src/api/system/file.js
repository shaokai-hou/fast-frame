import request from '@/utils/request'
import { download, getBlob } from '@/utils/request'

/**
 * 分页查询文件列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function listFile(params) {
  return request({
    url: '/system/file/list',
    method: 'get',
    params
  })
}

/**
 * 上传文件
 * @param {File} file 文件对象
 * @returns {Promise}
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传多个文件
 * @param {File[]} files 文件数组
 * @returns {Promise}
 */
export function uploadFiles(files) {
  const formData = new FormData()
  files.forEach(file => formData.append('files', file))
  return request({
    url: '/system/file/uploads',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 下载文件（带认证）
 * @param {string} objectName 对象名称
 * @param {string} filename 保存的文件名
 * @returns {Promise}
 */
export function downloadFile(objectName, filename) {
  return download(`/system/file/download/${objectName}`, filename)
}

/**
 * 删除文件
 * @param {number} id 文件ID
 * @returns {Promise}
 */
export function deleteFile(id) {
  return request({
    url: `/system/file/${id}`,
    method: 'delete'
  })
}

/**
 * 获取文件内容（用于预览）
 * @param {string} objectName 对象名称
 * @returns {Promise<Blob>}
 */
export function getFileBlob(objectName) {
  return getBlob(`/system/file/download/${objectName}`)
}