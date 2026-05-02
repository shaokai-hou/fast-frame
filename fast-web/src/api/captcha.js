/**
 * 验证码 API
 * 使用独立 axios 实例，不经过全局拦截器
 * 因为验证失败时 code=500，但需要组件自行处理
 */
import axios from 'axios'
import { getToken } from '@/utils/auth'

// 独立 axios 实例，不使用全局拦截器
const captchaRequest = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    timeout: 10000
})

// 请求拦截器 - 添加 token
captchaRequest.interceptors.request.use(
    config => {
        const token = getToken()
        if (token) {
            config.headers['sa-token'] = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器 - 直接返回原始响应数据，适配为 ResponseModel 格式
captchaRequest.interceptors.response.use(
    response => {
        const res = response.data
        // 适配 Result 格式为 ResponseModel 格式
        // 成功: code=200 -> repCode='0000'
        // 失败: code!=200 -> repCode='9999'
        return {
            repCode: res.code === 200 ? '0000' : '9999',
            repMsg: res.msg,
            repData: res.data
        }
    },
    error => {
        // 网络错误等
        return {
            repCode: '9999',
            repMsg: error.message || '请求失败',
            repData: null
        }
    }
)

/**
 * 获取验证图片以及 token
 *
 * @param data 验证码数据
 * @return Promise
 */
export function reqGet(data) {
    return captchaRequest({
        url: '/captcha/get',
        method: 'post',
        data
    })
}

/**
 * 滑动或者点选验证
 *
 * @param data 验证数据
 * @return Promise
 */
export function reqCheck(data) {
    return captchaRequest({
        url: '/captcha/check',
        method: 'post',
        data
    })
}