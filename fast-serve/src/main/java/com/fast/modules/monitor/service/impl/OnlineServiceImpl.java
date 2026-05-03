package com.fast.modules.monitor.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.modules.monitor.domain.query.OnlineUserQuery;
import com.fast.modules.monitor.domain.vo.OnlineUserVO;
import com.fast.modules.monitor.service.OnlineService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OnlineServiceImpl implements OnlineService {

    @Override
    public List<OnlineUserVO> listOnlineUsers(OnlineUserQuery query) {
        List<OnlineUserVO> result = new ArrayList<>();

        // 使用 searchSessionId 获取所有登录用户的 session
        List<String> sessionIds = StpUtil.searchSessionId("", 0, -1, false);

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

                // 获取 IP
                String ip = (String) StpUtil.getSessionBySessionId(sessionId).get("ip");
                user.setIp(ip);

                // 获取当前有效 token
                String token = StpUtil.getTokenValueByLoginId(user.getUserId());
                user.setTokenId(token);

                result.add(user);
            } catch (Exception e) {
                log.warn("获取 session {} 失败: {}", sessionId, e.getMessage());
            }
        }

        return result;
    }

    @Override
    public void forceLogout(String tokenId) {
        StpUtil.kickoutByTokenValue(tokenId);
    }
}