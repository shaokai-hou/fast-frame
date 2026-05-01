package com.fast.modules.system.domain.query;

import lombok.Data;

/**
 * 通知公告查询DTO
 *
 * @author fast-frame
 */
@Data
public class NoticeQuery {

    /**
     * 公告标题
     */
    private String noticeTitle;

    /**
     * 公告类型
     */
    private String noticeType;

    /**
     * 状态
     */
    private String status;
}