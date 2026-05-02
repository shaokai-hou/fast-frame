package com.fast.modules.auth.controller;

import com.fast.common.result.Result;
import com.fast.common.util.IpUtils;
import com.xingyuv.captcha.model.common.ResponseModel;
import com.xingyuv.captcha.model.vo.CaptchaVO;
import com.xingyuv.captcha.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        data.setBrowserInfo(IpUtils.getBrowserInfo(request));
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
        data.setBrowserInfo(IpUtils.getBrowserInfo(request));
        ResponseModel response = captchaService.check(data);
        if (response.isSuccess()) {
            return Result.success(response.getRepData());
        }
        return Result.fail(response.getRepMsg());
    }
}