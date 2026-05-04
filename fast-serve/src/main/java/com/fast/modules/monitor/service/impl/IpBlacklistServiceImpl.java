package com.fast.modules.monitor.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fast.common.constant.RedisConstants;
import com.fast.common.util.IpUtils;
import com.fast.framework.helper.RedisHelper;
import com.fast.modules.monitor.domain.dto.IpBlacklistDTO;
import com.fast.modules.monitor.domain.entity.IpBlacklist;
import com.fast.modules.monitor.domain.query.IpBlacklistQuery;
import com.fast.modules.monitor.domain.vo.IpBlacklistVO;
import com.fast.modules.monitor.mapper.IpBlacklistMapper;
import com.fast.modules.monitor.service.IpBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * IP黑名单Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IpBlacklistServiceImpl implements IpBlacklistService {

    private final IpBlacklistMapper ipBlacklistMapper;

    @Override
    public IPage<IpBlacklistVO> pageList(IpBlacklistQuery query) {
        Page<IpBlacklistVO> page = Page.of(query.getPageNum(), query.getPageSize());
        return ipBlacklistMapper.selectPageList(page, query);
    }

    @Override
    public IpBlacklist getById(Long id) {
        return ipBlacklistMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(IpBlacklistDTO dto) {
        IpBlacklist entity = new IpBlacklist();
        entity.setIpAddress(dto.getIpAddress());
        entity.setIpType(dto.getIpType());
        entity.setReason(dto.getReason());
        entity.setExpireTime(dto.getExpireTime());
        entity.setStatus(Objects.isNull(dto.getStatus()) ? "0" : dto.getStatus());
        entity.setCreateBy(StpUtil.getLoginIdAsLong());
        entity.setCreateTime(LocalDateTime.now());
        ipBlacklistMapper.insert(entity);

        // 刷新缓存
        refreshCache();

        log.info("[IpBlacklist] 新增IP黑名单: ip={}, type={}", dto.getIpAddress(), dto.getIpType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(IpBlacklistDTO dto) {
        IpBlacklist entity = new IpBlacklist();
        entity.setId(dto.getId());
        entity.setIpAddress(dto.getIpAddress());
        entity.setIpType(dto.getIpType());
        entity.setReason(dto.getReason());
        entity.setExpireTime(dto.getExpireTime());
        entity.setStatus(dto.getStatus());
        entity.setUpdateBy(StpUtil.getLoginIdAsLong());
        entity.setUpdateTime(LocalDateTime.now());
        ipBlacklistMapper.updateById(entity);

        // 刷新缓存
        refreshCache();

        log.info("[IpBlacklist] 修改IP黑名单: id={}, ip={}", dto.getId(), dto.getIpAddress());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        ipBlacklistMapper.deleteBatchIds(ids);

        // 刷新缓存
        refreshCache();

        log.info("[IpBlacklist] 删除IP黑名单: ids={}", ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, String status) {
        IpBlacklist entity = new IpBlacklist();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdateBy(StpUtil.getLoginIdAsLong());
        entity.setUpdateTime(LocalDateTime.now());
        ipBlacklistMapper.updateById(entity);

        // 刷新缓存
        refreshCache();

        log.info("[IpBlacklist] 修改IP黑名单状态: id={}, status={}", id, status);
    }

    @Override
    public void refreshCache() {
        // 查询所有启用的黑名单
        List<IpBlacklistVO> list = ipBlacklistMapper.selectActiveList();

        // 缓存到 Redis（永不过期，手动刷新）
        RedisHelper.setJson(RedisConstants.IP_BLACKLIST_KEY, list);

        log.info("[IpBlacklist] 刷新缓存完成，共{}条记录", list.size());
    }

    @Override
    public boolean isBlocked(String ip) {
        // 从 Redis 获取黑名单列表
        String json = RedisHelper.get(RedisConstants.IP_BLACKLIST_KEY);

        // 如果缓存为空，先刷新缓存
        if (Objects.isNull(json) || json.isEmpty()) {
            refreshCache();
            json = RedisHelper.get(RedisConstants.IP_BLACKLIST_KEY);
            if (Objects.isNull(json) || json.isEmpty()) {
                return false;
            }
        }

        List<IpBlacklistVO> blacklist = JSON.parseArray(json, IpBlacklistVO.class);
        if (Objects.isNull(blacklist) || blacklist.isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        for (IpBlacklistVO item : blacklist) {
            // 检查是否过期
            if (Objects.nonNull(item.getExpireTime()) && now.isAfter(item.getExpireTime())) {
                continue;
            }

            // 根据类型匹配
            if ("single".equals(item.getIpType())) {
                // 单IP精确匹配
                if (ip.equals(item.getIpAddress())) {
                    return true;
                }
            } else if ("cidr".equals(item.getIpType())) {
                // CIDR 网段匹配
                if (IpUtils.isIpInCidr(ip, item.getIpAddress())) {
                    return true;
                }
            }
        }

        return false;
    }
}