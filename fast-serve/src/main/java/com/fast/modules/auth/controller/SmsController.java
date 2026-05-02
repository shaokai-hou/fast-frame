package com.fast.modules.auth.controller;

import com.fast.common.result.Result;
import com.fast.modules.auth.domain.dto.SmsDTO;
import com.fast.modules.auth.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 短信验证码控制器
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    /**
     * 发送登录验证码
     *
     * @param dto 短信参数
     * @return 发送结果
     */
    @PostMapping("/sendLoginCode")
    public Result<Void> sendLoginCode(@RequestBody @Valid SmsDTO dto) {
        smsService.sendCode(dto.getPhone());
        return Result.success();
    }
}