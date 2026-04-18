package com.fast.modules.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.fast.common.constant.RedisKeyConstants;
import com.fast.modules.system.domain.dto.OnlineUserQuery;
import com.fast.modules.system.domain.dto.OnlineUserVO;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.service.OnlineService;
import com.fast.modules.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<OnlineUserVO> listOnlineUsers(OnlineUserQuery query) {
        List<OnlineUserVO> result = new ArrayList<>();

        // 使用 SCAN 命令替代 KEYS，避免阻塞 Redis
        Set<String> keys = scanKeys(RedisKeyConstants.SA_TOKEN_PREFIX);
        if (keys.isEmpty()) {
            return result;
        }

        // 收集所有用户 ID，避免 N+1 查询
        Map<String, Long> tokenUserIdMap = new HashMap<>();
        List<Long> userIds = new ArrayList<>();

        for (String key : keys) {
            String token = key.substring(RedisKeyConstants.SA_TOKEN_PREFIX.length());
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId != null) {
                Long userId = Long.parseLong(loginId.toString());
                tokenUserIdMap.put(token, userId);
                userIds.add(userId);
            }
        }

        if (userIds.isEmpty()) {
            return result;
        }

        // 批量查询用户信息
        List<User> users = userService.listByIds(userIds);
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }

        // 构建结果
        for (Map.Entry<String, Long> entry : tokenUserIdMap.entrySet()) {
            String token = entry.getKey();
            Long userId = entry.getValue();
            User user = userMap.get(userId);

            if (user == null) {
                continue;
            }

            // 过滤用户名
            if (StrUtil.isNotBlank(query.getUsername()) && !user.getUsername().contains(query.getUsername())) {
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

    /**
     * 使用 SCAN 命令遍历 key，避免 KEYS 命令阻塞 Redis
     *
     * @param pattern key 匹配模式
     * @return 匹配的 key 集合
     */
    private Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        try {
            org.springframework.data.redis.core.Cursor<String> cursor = redisTemplate.scan(
                    org.springframework.data.redis.core.ScanOptions.scanOptions()
                            .match(pattern + "*")
                            .count(100)
                            .build()
            );
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
            cursor.close();
        } catch (Exception e) {
            log.error("[OnlineService] SCAN 命令执行失败: {}", e.getMessage());
        }
        return keys;
    }
}