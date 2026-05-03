package com.fast.modules.monitor.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.common.constant.RedisConstants;
import com.fast.framework.helper.RedisHelper;
import com.fast.modules.monitor.domain.query.OnlineUserQuery;
import com.fast.modules.monitor.domain.vo.OnlineUserVO;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.monitor.service.OnlineService;
import com.fast.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 在线用户Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineServiceImpl implements OnlineService {

    private final UserService userService;

    @Override
    public List<OnlineUserVO> listOnlineUsers(OnlineUserQuery query) {
        List<OnlineUserVO> result = new ArrayList<>();

        // 使用 session key 搜索（sa-token:login:session:{loginId}）
        // is-concurrent=false 时，一个用户只有一个 session，比 token key 更稳定
        Set<String> sessionKeys = RedisHelper.scan(RedisConstants.SA_TOKEN_SESSION_PREFIX + "*");
        if (sessionKeys.isEmpty()) {
            return result;
        }

        // 收集用户 ID
        List<Long> userIds = new ArrayList<>();
        for (String key : sessionKeys) {
            // 从 key 中提取 loginId: sa-token:login:session:{loginId}
            String loginId = key.substring(RedisConstants.SA_TOKEN_SESSION_PREFIX.length());
            try {
                userIds.add(Long.parseLong(loginId));
            } catch (NumberFormatException e) {
                log.warn("无效的 loginId: {}", loginId);
            }
        }

        if (userIds.isEmpty()) {
            return result;
        }

        // 批量查询用户信息
        List<User> users = userService.listByIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 构建结果 - 每个用户一条记录
        for (Long userId : userIds) {
            User user = userMap.get(userId);

            if (user == null) {
                continue;
            }

            // 过滤用户名
            if (StrUtil.isNotBlank(query.getUsername()) && !user.getUsername().contains(query.getUsername())) {
                continue;
            }

            // 获取当前有效 token（is-concurrent=false 时每个用户只有一个）
            String token = StpUtil.getTokenValueByLoginId(userId);
            if (token == null) {
                continue;
            }

            OnlineUserVO vo = new OnlineUserVO();
            vo.setTokenId(token);
            vo.setUserId(userId);
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());

            // 获取登录时间
            long createTime = StpUtil.getTokenSessionByToken(token).getCreateTime();
            vo.setLoginTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(createTime), ZoneId.systemDefault()));

            // 获取登录 IP
            String ip = (String) StpUtil.getTokenSessionByToken(token).get("ip");
            vo.setIp(ip);

            result.add(vo);
        }

        return result;
    }

    @Override
    public void forceLogout(String tokenId) {
        StpUtil.kickoutByTokenValue(tokenId);
    }
}