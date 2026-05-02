package com.fast.modules.log.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.Constants;
import com.fast.common.result.PageRequest;
import com.fast.modules.log.domain.query.LoginLogQuery;
import com.fast.modules.log.domain.vo.LoginLogExportVO;
import com.fast.modules.log.domain.vo.LoginLogVO;
import com.fast.modules.log.domain.entity.LoginLog;
import com.fast.modules.log.mapper.LoginLogMapper;
import com.fast.modules.log.service.LoginLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 查询登录日志列表（用于导出）
     *
     * @param query 查询条件
     * @return 登录日志导出数据列表
     */
    @Override
    public List<LoginLogExportVO> listLoginLogForExport(LoginLogQuery query) {
        LambdaQueryWrapper<LoginLog> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(query.getUsername()), LoginLog::getUsername, query.getUsername());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), LoginLog::getStatus, query.getStatus());
        wrapper.orderByDesc(LoginLog::getLoginTime);

        List<LoginLog> list = list(wrapper);
        return list.stream().map(loginLog -> {
            LoginLogExportVO vo = BeanUtil.copyProperties(loginLog, LoginLogExportVO.class);
            vo.setStatus(Constants.SUCCESS.equals(loginLog.getStatus()) ? "成功" : "失败");
            return vo;
        }).collect(Collectors.toList());
    }
}