package com.fast.modules.monitor.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fast.common.result.PageRequest;
import com.fast.modules.monitor.domain.query.OnlineUserQuery;
import com.fast.modules.monitor.domain.vo.OnlineUserVO;
import com.fast.modules.monitor.service.OnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线用户Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
public class OnlineServiceImpl implements OnlineService {

    @Override
    public IPage<OnlineUserVO> pageOnlineUsers(OnlineUserQuery query, PageRequest pageRequest) {
        // 获取总数
        List<String> allSessionIds = StpUtil.searchSessionId("", 0, -1, false);
        long total = allSessionIds.size();

        // 分页参数
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        int start = (pageNum - 1) * pageSize;

        // 分页获取 session
        List<String> sessionIds = StpUtil.searchSessionId("", start, pageSize, false);

        List<OnlineUserVO> list = new ArrayList<>();
        for (String sessionId : sessionIds) {
            try {
                // 从 session 中获取存储的用户信息
                OnlineUserVO user = StpUtil.getSessionBySessionId(sessionId)
                        .getModel("online_user", OnlineUserVO.class);

                if (user == null) {
                    continue;
                }

                // 过滤用户名
                if (StrUtil.isNotBlank(query.getUsername()) &&
                    !user.getUsername().contains(query.getUsername())) {
                    continue;
                }

                list.add(user);
            } catch (Exception e) {
                log.warn("获取 session {} 失败: {}", sessionId, e.getMessage());
            }
        }

        // 返回分页结果
        Page<OnlineUserVO> page = new Page<>(pageNum, pageSize);
        page.setRecords(list);
        page.setTotal(total);
        return page;
    }
}