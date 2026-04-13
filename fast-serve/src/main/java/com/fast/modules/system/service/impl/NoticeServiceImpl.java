package com.fast.modules.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.result.PageResult;
import com.fast.modules.system.domain.dto.NoticeDTO;
import com.fast.modules.system.domain.dto.NoticeQuery;
import com.fast.modules.system.domain.entity.Notice;
import com.fast.modules.system.domain.vo.NoticeVO;
import com.fast.modules.system.mapper.NoticeMapper;
import com.fast.modules.system.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知公告Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public PageResult<NoticeVO> pageNotices(NoticeQuery query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getNoticeTitle() != null, Notice::getNoticeTitle, query.getNoticeTitle());
        wrapper.eq(query.getNoticeType() != null, Notice::getNoticeType, query.getNoticeType());
        wrapper.eq(query.getStatus() != null, Notice::getStatus, query.getStatus());
        wrapper.orderByDesc(Notice::getCreateTime);

        Page<Notice> page = page(new Page<>(pageNum, pageSize), wrapper);

        List<NoticeVO> voList = page.getRecords().stream().map(notice -> {
            NoticeVO vo = new NoticeVO();
            BeanUtils.copyProperties(notice, vo);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal());
    }

    @Override
    public void addNotice(NoticeDTO dto) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        notice.setCreateBy(StpUtil.getLoginIdAsLong());
        notice.setCreateTime(LocalDateTime.now());
        notice.setStatus("0");
        save(notice);
    }

    @Override
    public void updateNotice(NoticeDTO dto) {
        Notice notice = getById(dto.getId());
        if (notice == null) {
            throw new RuntimeException("公告不存在");
        }
        BeanUtils.copyProperties(dto, notice);
        notice.setUpdateBy(StpUtil.getLoginIdAsLong());
        notice.setUpdateTime(LocalDateTime.now());
        updateById(notice);
    }

    @Override
    public void deleteNotices(List<Long> ids) {
        removeByIds(ids);
    }
}