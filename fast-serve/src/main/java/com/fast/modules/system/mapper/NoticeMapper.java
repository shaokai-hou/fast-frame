package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.domain.dto.NoticeQuery;
import com.fast.modules.system.domain.dto.NoticeVO;
import com.fast.modules.system.domain.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 分页查询通知公告列表
     *
     * @param page  分页对象
     * @param query 查询参数
     * @return 通知公告分页结果
     */
    IPage<NoticeVO> selectNoticePage(IPage<NoticeVO> page, NoticeQuery query);
}