package com.fast.modules.monitor.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fast.modules.monitor.domain.query.OnlineUserQuery;
import com.fast.modules.monitor.domain.vo.OnlineUserVO;
import com.fast.modules.monitor.service.OnlineService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 在线用户Service实现
 *
 * @author fast-frame
 */
@Service
public class OnlineServiceImpl implements OnlineService {

    @Override
    public IPage<OnlineUserVO> pageOnlineUsers(OnlineUserQuery query) {
        // 获取所有登录用户的 sessionIds
        List<String> sessionIds = StpUtil.searchSessionId("", 0, -1, false);

        // Stream 分页 + 映射 + 过滤
        List<OnlineUserVO> list = sessionIds.stream()
                .skip((query.getPageNum() - 1) * query.getPageSize())
                .limit(query.getPageSize())
                .map(sessionId -> StpUtil.getSessionBySessionId(sessionId)
                        .getModel("online_user", OnlineUserVO.class))
                .filter(Objects::nonNull)
                .filter(user -> StrUtil.isBlank(query.getUsername()) ||
                        user.getUsername().contains(query.getUsername()))
                .collect(Collectors.toList());

        // 返回分页结果
        Page<OnlineUserVO> page = Page.of(query.getPageNum(), query.getPageSize());
        page.setRecords(list);
        page.setTotal(sessionIds.size());
        return page;
    }
}