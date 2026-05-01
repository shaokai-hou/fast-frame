package com.fast.modules.auth.service;

import com.fast.modules.auth.domain.dto.LoginDTO;
import com.fast.modules.auth.domain.vo.LoginVO;
import com.fast.modules.auth.domain.vo.UserInfoVO;
import com.fast.modules.auth.domain.vo.RoutesVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证Service
 *
 * @author fast-frame
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param dto     登录参数
     * @param request HTTP请求
     * @return 登录结果
     */
    LoginVO login(LoginDTO dto, HttpServletRequest request);

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
}