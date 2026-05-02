package com.fast.modules.message.domain.vo;

import lombok.Data;

/**
 * 未读消息统计VO
 *
 * @author fast-frame
 */
@Data
public class UnreadCountVO {

    /**
     * 未读消息总数
     */
    private Long total;

    /**
     * 系统消息未读数
     */
    private Long systemCount;

    /**
     * 私人消息未读数
     */
    private Long privateCount;

    /**
     * 通知未读数
     */
    private Long noticeCount;
}