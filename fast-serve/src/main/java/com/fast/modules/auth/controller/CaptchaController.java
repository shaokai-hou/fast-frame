package com.fast.modules.auth.controller;

import com.fast.common.result.Result;
import com.xingyuv.captcha.model.common.ResponseModel;
import com.xingyuv.captcha.model.vo.CaptchaVO;
import com.xingyuv.captcha.service.CaptchaService;
import com.xingyuv.captcha.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 滑块验证码控制器
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 获取验证码
     *
     * @param data    验证码数据
     * @param request HTTP请求
     * @return 验证码响应结果
     */
    @PostMapping("/get")
    public Result<Object> get(@RequestBody CaptchaVO data, HttpServletRequest request) {
        data.setBrowserInfo(getRemoteId(request));
        ResponseModel response = captchaService.get(data);
        if (response.isSuccess()) {
            return Result.success(response.getRepData());
        }
        return Result.fail(response.getRepMsg());
    }

    /**
     * 校验验证码
     *
     * @param data    验证码数据
     * @param request HTTP请求
     * @return 校验结果响应结果
     */
    @PostMapping("/check")
    public Result<Object> check(@RequestBody CaptchaVO data, HttpServletRequest request) {
        data.setBrowserInfo(getRemoteId(request));
        ResponseModel response = captchaService.check(data);
        if (response.isSuccess()) {
            return Result.success(response.getRepData());
        }
        return Result.fail(response.getRepMsg());
    }

    /**
     * 获取客户端远程标识
     *
     * @param request HTTP请求
     * @return 远程标识(ip+ua)
     */
    public static String getRemoteId(HttpServletRequest request) {
        String xfwd = request.getHeader("X-Forwarded-For");
        String ip = getRemoteIpFromXfwd(xfwd);
        String ua = request.getHeader("user-agent");
        if (StringUtils.isNotBlank(ip)) {
            return ip + ua;
        }
        return request.getRemoteAddr() + ua;
    }

    /**
     * 从 X-Forwarded-For 头获取远程 IP
     *
     * @param xfwd X-Forwarded-For 头内容
     * @return 远程 IP
     */
    private static String getRemoteIpFromXfwd(String xfwd) {
        if (StringUtils.isNotBlank(xfwd)) {
            String[] ipList = xfwd.split(",");
            return StringUtils.trim(ipList[0]);
        }
        return null;
    }
}