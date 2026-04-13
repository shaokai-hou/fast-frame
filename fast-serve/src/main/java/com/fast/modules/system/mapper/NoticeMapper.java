package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.system.domain.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}