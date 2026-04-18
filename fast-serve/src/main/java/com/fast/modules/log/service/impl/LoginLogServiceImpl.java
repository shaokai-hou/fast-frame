package com.fast.modules.log.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.result.PageRequest;
import com.fast.modules.log.domain.dto.LoginLogQuery;
import com.fast.modules.log.domain.dto.LoginLogVO;
import com.fast.modules.log.domain.entity.LoginLog;
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

    /**
     * 分页查询登录日志列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 登录日志分页结果
     */
    @Override
    public IPage<LoginLogVO> pageLoginLogs(LoginLogQuery query, PageRequest pageRequest) {
        return baseMapper.selectLoginLogPage(pageRequest.toPage(), query);
    }
}