package com.fast.framework.helper;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis辅助类
 * 使用 StringRedisTemplate + Fastjson 序列化复杂对象
 *
 * @author fast-frame
 */
@Slf4j
public class RedisHelper {

    private static StringRedisTemplate getRedisTemplate() {
        return SpringUtil.getBean(StringRedisTemplate.class);
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值（String）
     */
    public static String get(String key) {
        return getRedisTemplate().opsForValue().get(key);
    }

    /**
     * 获取 JSON 缓存值并转换为指定类型
     *
     * @param key   缓存键
     * @param clazz 目标类型
     * @param <T>   类型泛型
     * @return 缓存值
     */
    public static <T> T getJson(String key, Class<T> clazz) {
        String json = get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 获取 JSON 缓存值并转换为指定类型（支持泛型类型）
     *
     * @param key  缓存键
     * @param type 目标类型
     * @param <T>  类型泛型
     * @return 缓存值
     */
    public static <T> T getJson(String key, TypeReference<T> type) {
        String json = get(key);
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json, type);
    }

    /**
     * 设置缓存值（永不过期）
     *
     * @param key   缓存键
     * @param value 缓存值（String）
     */
    public static void set(String key, String value) {
        getRedisTemplate().opsForValue().set(key, value);
    }

    /**
     * 设置缓存值（秒为单位过期）
     *
     * @param key     缓存键
     * @param value   缓存值（String）
     * @param timeout 过期时间（秒）
     */
    public static void set(String key, String value, long timeout) {
        getRedisTemplate().opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置缓存值（指定时间单位）
     *
     * @param key     缓存键
     * @param value   缓存值（String）
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public static void set(String key, String value, long timeout, TimeUnit unit) {
        getRedisTemplate().opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置 JSON 缓存值（永不过期）
     *
     * @param key   缓存键
     * @param value 缓存值（任意对象）
     */
    public static void setJson(String key, Object value) {
        getRedisTemplate().opsForValue().set(key, JSON.toJSONString(value));
    }

    /**
     * 设置 JSON 缓存值（秒为单位过期）
     *
     * @param key     缓存键
     * @param value   缓存值（任意对象）
     * @param timeout 过期时间（秒）
     */
    public static void setJson(String key, Object value, long timeout) {
        getRedisTemplate().opsForValue().set(key, JSON.toJSONString(value), timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置 JSON 缓存值（指定时间单位）
     *
     * @param key     缓存键
     * @param value   缓存值（任意对象）
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public static void setJson(String key, Object value, long timeout, TimeUnit unit) {
        getRedisTemplate().opsForValue().set(key, JSON.toJSONString(value), timeout, unit);
    }

    /**
     * 删除单个缓存
     *
     * @param key 缓存键
     * @return 是否删除成功
     */
    public static boolean delete(String key) {
        return Boolean.TRUE.equals(getRedisTemplate().delete(key));
    }

    /**
     * 删除多个缓存
     *
     * @param keys 缓存键集合
     * @return 删除的数量
     */
    public static long delete(Collection<String> keys) {
        Long count = getRedisTemplate().delete(keys);
        return count != null ? count : 0L;
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public static boolean hasKey(String key) {
        return Boolean.TRUE.equals(getRedisTemplate().hasKey(key));
    }

    /**
     * 设置过期时间（秒为单位）
     *
     * @param key     缓存键
     * @param timeout 过期时间（秒）
     * @return 是否设置成功
     */
    public static boolean expire(String key, long timeout) {
        return Boolean.TRUE.equals(getRedisTemplate().expire(key, timeout, TimeUnit.SECONDS));
    }

    /**
     * 设置过期时间（指定时间单位）
     *
     * @param key     缓存键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    public static boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(getRedisTemplate().expire(key, timeout, unit));
    }

    /**
     * 获取过期时间（秒）
     *
     * @param key 缓存键
     * @return 过期时间（秒），-2表示不存在，-1表示永不过期
     */
    public static long getExpire(String key) {
        Long expire = getRedisTemplate().getExpire(key, TimeUnit.SECONDS);
        return expire != null ? expire : -2L;
    }

    /**
     * 自增1
     *
     * @param key 缓存键
     * @return 自增后的值
     */
    public static long incr(String key) {
        return incr(key, 1);
    }

    /**
     * 自增指定值
     *
     * @param key   缓存键
     * @param delta 增量
     * @return 自增后的值
     */
    public static long incr(String key, long delta) {
        Long value = getRedisTemplate().opsForValue().increment(key, delta);
        return value != null ? value : 0L;
    }

    /**
     * 自减1
     *
     * @param key 缓存键
     * @return 自减后的值
     */
    public static long decr(String key) {
        return incr(key, -1);
    }

    /**
     * 设置缓存值（仅当key不存在时设置，SETNX）
     *
     * @param key     缓存键
     * @param value   缓存值（String）
     * @param timeout 过期时间（秒）
     * @return 是否设置成功（true表示key不存在且设置成功）
     */
    public static boolean setIfAbsent(String key, String value, long timeout) {
        Boolean result = getRedisTemplate().opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
        return result != null && result;
    }

    /**
     * 扫描匹配的key
     *
     * @param pattern key匹配模式
     * @return 匹配的key集合
     */
    public static Set<String> scan(String pattern) {
        Set<String> keys = new HashSet<>();
        try {
            Cursor<String> cursor = getRedisTemplate().scan(
                    ScanOptions.scanOptions()
                            .match(pattern)
                            .count(100)
                            .build()
            );
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
            cursor.close();
        } catch (Exception e) {
            log.error("[RedisHelper] SCAN命令执行失败: {}", e.getMessage());
        }
        return keys;
    }

    /**
     * 获取key的类型
     *
     * @param key 缓存键
     * @return 类型字符串（string, hash, list, set, zset等），不存在返回null
     */
    public static String type(String key) {
        DataType dataType = getRedisTemplate().type(key);
        return dataType != null ? dataType.code() : null;
    }

    /**
     * 获取Redis服务器信息
     *
     * @return Redis信息Properties，获取失败返回null
     */
    public static Properties getInfo() {
        RedisConnectionFactory factory = getRedisTemplate().getConnectionFactory();
        if (factory == null) {
            return null;
        }
        return factory.getConnection().serverCommands().info();
    }
}