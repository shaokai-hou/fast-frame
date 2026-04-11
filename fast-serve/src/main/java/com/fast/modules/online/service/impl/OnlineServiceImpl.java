package com.fast.modules.online.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.modules.online.service.OnlineService;
import com.fast.modules.online.vo.OnlineUserVO;
import com.fast.modules.system.entity.User;
import com.fast.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 在线用户Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class OnlineServiceImpl implements OnlineService {

    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Sa-Token 在 Redis 中存储 token session 的 key 前缀
     */
    private static final String SA_TOKEN_KEY_PREFIX = "sa-token:login:token:";

    @Override
    public List<OnlineUserVO> listOnlineUsers(String username) {
        List<OnlineUserVO> result = new ArrayList<>();

        // 直接从 Redis 获取所有 token session 的 key
        Set<String> keys = redisTemplate.keys(SA_TOKEN_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return result;
        }

        for (String key : keys) {
            // 从 key 中提取 token 值: satoken:login:token:{token}
            String token = key.substring(SA_TOKEN_KEY_PREFIX.length());

            // 获取登录ID
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                continue;
            }

            Long userId = Long.parseLong(loginId.toString());
            User user = userService.getById(userId);
            if (user == null) {
                continue;
            }

            // 过滤用户名
            if (StrUtil.isNotBlank(username) && !user.getUsername().contains(username)) {
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