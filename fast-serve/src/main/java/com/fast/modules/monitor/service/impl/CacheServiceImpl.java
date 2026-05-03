package com.fast.modules.monitor.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fast.common.constant.RedisConstants;
import com.fast.modules.monitor.domain.query.CacheQuery;
import com.fast.modules.monitor.domain.vo.CacheInfoVO;
import com.fast.modules.monitor.domain.vo.CacheKeyVO;
import com.fast.modules.monitor.domain.vo.CachePrefixVO;
import com.fast.modules.monitor.service.CacheService;
import com.fast.framework.helper.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 缓存管理Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    /**
     * 缓存前缀列表（从常量类反射获取）
     */
    private List<CachePrefixVO> cachePrefixes;

    /**
     * 启动时通过反射获取 RedisKeyConstants 中所有 String 常量
     */
    @PostConstruct
    public void initPrefixes() {
        cachePrefixes = new ArrayList<>();
        Field[] fields = RedisConstants.class.getDeclaredFields();
        for (Field field : fields) {
            // 获取所有 public static final String 常量
            if (field.getType() == String.class
                && Modifier.isStatic(field.getModifiers())
                && Modifier.isFinal(field.getModifiers())) {
                try {
                    String name = field.getName();
                    String value = (String) field.get(null);
                    // 去掉末尾冒号作为查询值
                    String queryValue = value.endsWith(":")
                        ? value.substring(0, value.length() - 1)
                        : value;
                    cachePrefixes.add(new CachePrefixVO(name, queryValue));
                } catch (IllegalAccessException e) {
                    log.warn("[CacheService] 无法访问常量字段: {}", field.getName());
                }
            }
        }
    }

    @Override
    public List<CachePrefixVO> getCachePrefixes() {
        return cachePrefixes;
    }

    @Override
    public IPage<CacheKeyVO> pageCacheKeys(CacheQuery query) {
        List<CacheKeyVO> allKeys = new ArrayList<>();

        // 遍历从常量类反射获取的缓存前缀
        for (CachePrefixVO prefixVO : cachePrefixes) {
            // 如果指定了 prefix 参数，只扫描匹配的前缀
            if (query.getPrefix() != null && !query.getPrefix().isEmpty()) {
                if (!prefixVO.getValue().equals(query.getPrefix())) {
                    continue;
                }
            }

            // 使用带冒号的完整前缀进行扫描
            String fullPrefix = prefixVO.getValue() + ":";
            Set<String> keys = scanKeys(fullPrefix);
            for (String key : keys) {
                CacheKeyVO vo = new CacheKeyVO();
                vo.setKey(key);
                vo.setPrefix(prefixVO.getValue());

                // 获取缓存类型
                String type = RedisHelper.type(key);
                vo.setType(type);

                // 获取过期时间
                long ttl = RedisHelper.getExpire(key);
                vo.setTtl(ttl);

                allKeys.add(vo);
            }
        }

        // 计算总数
        long total = allKeys.size();

        // 内存分页
        int fromIndex = Math.toIntExact((query.getPageNum() - 1) * query.getPageSize());
        int toIndex = Math.toIntExact(Math.min((query.getPageNum() - 1) * query.getPageSize() + query.getPageSize(), allKeys.size()));

        List<CacheKeyVO> pageData = fromIndex < allKeys.size()
            ? allKeys.subList(fromIndex, toIndex)
            : new ArrayList<>();

        Page<CacheKeyVO> resultPage = Page.of(query.getPageNum(), query.getPageSize());
        resultPage.setRecords(pageData);
        resultPage.setTotal(total);
        return resultPage;
    }

    @Override
    public CacheInfoVO getCacheInfo(String key) {
        CacheInfoVO vo = new CacheInfoVO();
        vo.setKey(key);

        // 获取缓存类型
        String type = RedisHelper.type(key);
        vo.setType(type);

        // 获取缓存值
        String value = RedisHelper.get(key);
        vo.setValue(value);

        // 获取过期时间
        long ttl = RedisHelper.getExpire(key);
        vo.setTtl(ttl);

        // 计算缓存大小
        if (Objects.nonNull(value)) {
            vo.setSize((long) value.length());
        } else {
            vo.setSize(0L);
        }

        return vo;
    }

    @Override
    public void deleteCache(String key) {
        RedisHelper.delete(key);
    }

    @Override
    public void clearAllCache() {
        // 清空业务缓存，排除 sa-token 相关（保留登录状态）
        for (CachePrefixVO prefixVO : cachePrefixes) {
            String value = prefixVO.getValue();
            // 排除 Sa-Token 相关前缀，避免清空登录会话
            if (value.contains("sa-token")) {
                continue;
            }
            String fullPrefix = value + ":";
            Set<String> keys = scanKeys(fullPrefix);
            if (!keys.isEmpty()) {
                RedisHelper.delete(keys);
            }
        }
    }

    @Override
    public String getRedisInfo() {
        Properties info = RedisHelper.getInfo();
        if (Objects.nonNull(info)) {
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
        return RedisHelper.scan(pattern + "*");
    }
}