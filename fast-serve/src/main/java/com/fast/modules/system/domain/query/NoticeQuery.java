package com.fast.modules.system.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 通知公告查询DTO
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeQuery extends PageRequest {

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