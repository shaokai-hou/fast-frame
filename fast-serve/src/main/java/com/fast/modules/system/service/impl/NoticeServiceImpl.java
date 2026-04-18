package com.fast.modules.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.Constants;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageRequest;
import com.fast.modules.system.domain.dto.NoticeDTO;
import com.fast.modules.system.domain.dto.NoticeQuery;
import com.fast.modules.system.domain.dto.NoticeVO;
import com.fast.modules.system.domain.entity.Notice;
import com.fast.modules.system.mapper.NoticeMapper;
import com.fast.modules.system.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 通知公告Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeMapper noticeMapper;

    /**
     * 分页查询公告列表
     *
     * @param pageRequest 分页参数
     * @param query       查询条件
     * @return 公告分页结果
     */
    @Override
    public IPage<NoticeVO> pageNotices(PageRequest pageRequest, NoticeQuery query) {
        return noticeMapper.selectNoticePage(pageRequest.toPage(), query);
    }

    /**
     * 新增公告
     *
     * @param dto 公告DTO
     */
    @Override
    public void addNotice(NoticeDTO dto) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(dto, notice);
        notice.setStatus(Constants.NORMAL);
        save(notice);
    }

    /**
     * 修改公告
     *
     * @param dto 公告DTO
     */
    @Override
    public void updateNotice(NoticeDTO dto) {
        Notice notice = getById(dto.getId());
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        BeanUtils.copyProperties(dto, notice);
        updateById(notice);
    }
}