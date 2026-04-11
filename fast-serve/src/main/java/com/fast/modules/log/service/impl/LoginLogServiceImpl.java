package com.fast.modules.log.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.result.PageResult;
import com.fast.modules.log.entity.LoginLog;
import com.fast.modules.log.mapper.LoginLogMapper;
import com.fast.modules.log.service.LoginLogService;
import org.springframework.stereotype.Service;

/**
 * 登录日志Service实现
 *
 * @author fast-frame
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public PageResult<LoginLog> listPage(LoginLog query, Integer pageNum, Integer pageSize) {
        Page<LoginLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getUsername()), LoginLog::getUsername, query.getUsername())
               .eq(StrUtil.isNotBlank(query.getStatus()), LoginLog::getStatus, query.getStatus())
               .orderByDesc(LoginLog::getLoginTime);
        Page<LoginLog> result = page(page, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal());
    }

    @Override
    public void clear() {
        baseMapper.delete(new LambdaQueryWrapper<>());
    }
}