/**
 * 验证码 API
 * 使用项目封装的 axios request
 * 适配 Result 格式为组件期望的 ResponseModel 格式
 */
import request from '@/utils/request'

/**
 * 获取验证图片以及 token
 *
 * @param data 验证码数据
 * @return Promise
 */
export function reqGet(data) {
    return request({
        url: '/captcha/get',
        method: 'post',
        data
    }).then(res => {
        // 适配 Result 格式为 ResponseModel 格式
        return {
            repCode: '0000',
            repMsg: res.msg,
            repData: res.data
        }
    })
}

/**
 * 滑动或者点选验证
 *
 * @param data 验证数据
 * @return Promise
 */
export function reqCheck(data) {
    return request({
        url: '/captcha/check',
        method: 'post',
        data
    }).then(res => {
        // 适配 Result 格式为 ResponseModel 格式
        return {
            repCode: '0000',
            repMsg: res.msg,
            repData: res.data
        }
    })
}