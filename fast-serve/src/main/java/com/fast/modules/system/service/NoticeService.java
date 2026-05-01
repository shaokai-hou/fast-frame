package com.fast.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageRequest;
import com.fast.modules.system.domain.dto.NoticeDTO;
import com.fast.modules.system.domain.query.NoticeQuery;
import com.fast.modules.system.domain.vo.NoticeVO;
import com.fast.modules.system.domain.entity.Notice;

/**
 * 通知公告Service
 *
 * @author fast-frame
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询公告列表
     *
     * @param pageRequest 分页参数
     * @param query       查询条件
     * @return 公告分页结果
     */
    IPage<NoticeVO> pageNotices(PageRequest pageRequest, NoticeQuery query);

    /**
     * 新增公告
     *
     * @param dto 公告DTO
     */
    void addNotice(NoticeDTO dto);

    /**
     * 修改公告
     *
     * @param dto 公告DTO
     */
    void updateNotice(NoticeDTO dto);
}