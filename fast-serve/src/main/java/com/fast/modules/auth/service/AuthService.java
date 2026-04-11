package com.fast.modules.auth.service;

import com.fast.modules.auth.domain.dto.LoginDTO;
import com.fast.modules.auth.domain.vo.CaptchaVO;
import com.fast.modules.auth.domain.vo.LoginVO;
import com.fast.modules.auth.domain.vo.RoutesVO;
import com.fast.modules.auth.domain.vo.UserInfoVO;

/**
 * 认证Service
 *
 * @author fast-frame
 */
public interface AuthService {

    /**
     * 生成验证码
     *
     * @return 验证码信息
     */
    CaptchaVO generateCaptcha();

    /**
     * 登录
     *
     * @param dto 登录参数
     * @param ip  登录IP
     * @return 登录结果
     */
    LoginVO login(LoginDTO dto, String ip);

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    UserInfoVO getUserInfo();

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    RoutesVO getRoutes();

    /**
     * 退出登录
     */
    void logout();
}