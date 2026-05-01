package com.fast.modules.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fast.common.result.Result;
import com.fast.framework.web.BaseController;
import com.fast.modules.auth.domain.dto.LoginDTO;
import com.fast.modules.auth.domain.vo.LoginVO;
import com.fast.modules.auth.domain.vo.RoutesVO;
import com.fast.modules.auth.domain.vo.UserInfoVO;
import com.fast.modules.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    /**
     * 登录
     *
     * @param dto     登录参数
     * @param request HTTP请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        return success(authService.login(dto, request));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/userInfo")
    public Result<UserInfoVO> userInfo() {
        return success(authService.getUserInfo());
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/routes")
    public Result<RoutesVO> routes() {
        return success(authService.getRoutes());
    }

    /**
     * 退出登录
     *
     * @return 成功结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return success();
    }
}