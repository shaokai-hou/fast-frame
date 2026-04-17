package com.fast.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageResult;
import com.fast.modules.system.domain.dto.NoticeDTO;
import com.fast.modules.system.domain.dto.NoticeQuery;
import com.fast.modules.system.domain.entity.Notice;
import com.fast.modules.system.domain.vo.NoticeVO;

import java.util.List;

/**
 * 通知公告Service
 *
 * @author fast-frame
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询公告列表
     *
     * @param query    查询条件
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 公告分页结果
     */
    PageResult<NoticeVO> pageNotices(NoticeQuery query, Integer pageNum, Integer pageSize);

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

    /**
     * 删除公告
     *
     * @param ids 公告ID列表
     */
    void deleteNotices(List<Long> ids);
}