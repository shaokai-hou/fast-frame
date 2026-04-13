package com.fast.modules.monitor.service.impl;

import com.fast.common.constant.RedisKeyConstants;
import com.fast.common.result.PageResult;
import com.fast.modules.monitor.domain.vo.CacheInfoVO;
import com.fast.modules.monitor.domain.vo.CacheKeyVO;
import com.fast.modules.monitor.service.CacheService;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 缓存管理Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存前缀列表（从常量类反射获取）
     */
    private List<String> cachePrefixes;

    /**
     * 启动时通过反射获取 RedisKeyConstants 中所有 PREFIX 常量
     */
    @PostConstruct
    public void initPrefixes() {
        cachePrefixes = new ArrayList<>();
        Field[] fields = RedisKeyConstants.class.getDeclaredFields();
        for (Field field : fields) {
            // 只获取以 _PREFIX 结尾的 public static final String 常量
            if (field.getName().endsWith("_PREFIX")
                && field.getType() == String.class
                && java.lang.reflect.Modifier.isStatic(field.getModifiers())
                && java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                try {
                    String prefix = (String) field.get(null);
                    cachePrefixes.add(prefix);
                } catch (IllegalAccessException e) {
                    log.warn("[CacheService] 无法访问常量字段: {}", field.getName());
                }
            }
        }
        log.info("[CacheService] 已加载 {} 个缓存前缀: {}", cachePrefixes.size(), cachePrefixes);
    }

    @Override
    public PageResult<CacheKeyVO> pageCacheKeys(String prefix, Integer pageNum, Integer pageSize) {
        List<CacheKeyVO> allKeys = new ArrayList<>();

        // 遍历从常量类反射获取的缓存前缀
        for (String p : cachePrefixes) {
            // 如果指定了 prefix 参数，只扫描匹配的前缀
            if (prefix != null && !prefix.isEmpty()) {
                String prefixBase = p.substring(0, p.length() - 1);
                if (!prefixBase.equals(prefix)) {
                    continue;
                }
            }

            Set<String> keys = scanKeys(p);
            for (String key : keys) {
                CacheKeyVO vo = new CacheKeyVO();
                vo.setKey(key);
                vo.setPrefix(p.substring(0, p.length() - 1));

                // 获取缓存类型
                String type = redisTemplate.type(key).code();
                vo.setType(type);

                // 获取过期时间
                Long ttl = redisTemplate.getExpire(key);
                vo.setTtl(ttl);

                allKeys.add(vo);
            }
        }

        // 计算总数
        long total = allKeys.size();

        // 内存分页
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allKeys.size());

        List<CacheKeyVO> pageData = fromIndex < allKeys.size()
            ? allKeys.subList(fromIndex, toIndex)
            : new ArrayList<>();

        return PageResult.of(pageData, total);
    }

    @Override
    public CacheInfoVO getCacheInfo(String key) {
        CacheInfoVO vo = new CacheInfoVO();
        vo.setKey(key);

        // 获取缓存类型
        String type = redisTemplate.type(key).code();
        vo.setType(type);

        // 获取缓存值
        Object value = redisTemplate.opsForValue().get(key);
        vo.setValue(value != null ? value.toString() : null);

        // 获取过期时间
        Long ttl = redisTemplate.getExpire(key);
        vo.setTtl(ttl);

        // 计算缓存大小 - 简化处理
        if (value != null) {
            vo.setSize((long) value.toString().length());
        } else {
            vo.setSize(0L);
        }

        return vo;
    }

    @Override
    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void clearAllCache() {
        // 清空业务缓存，排除 sa-token 相关（保留登录状态）
        for (String prefix : cachePrefixes) {
            // 排除 Sa-Token 相关前缀，避免清空登录会话
            if (prefix.contains("sa-token")) {
                continue;
            }
            Set<String> keys = scanKeys(prefix);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        }
    }

    @Override
    public String getRedisInfo() {
        Properties info = redisTemplate.getConnectionFactory().getConnection().serverCommands().info();
        if (info != null) {
            StringBuilder sb = new StringBuilder();
            for (String key : info.stringPropertyNames()) {
                sb.append(key).append(": ").append(info.getProperty(key)).append("\n");
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 使用SCAN命令遍历key
     *
     * @param pattern key匹配模式
     * @return 匹配的key集合
     */
    private Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        try {
            Cursor<String> cursor = redisTemplate.scan(
                ScanOptions.scanOptions()
                    .match(pattern + "*")
                    .count(100)
                    .build()
            );
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
            cursor.close();
        } catch (Exception e) {
            log.error("[CacheService] SCAN命令执行失败: {}", e.getMessage());
        }
        return keys;
    }
}