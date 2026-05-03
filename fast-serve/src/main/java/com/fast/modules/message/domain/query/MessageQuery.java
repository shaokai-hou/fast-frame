package com.fast.modules.message.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 消息查询条件
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageQuery extends PageRequest {

    /**
     * 消息标题
     */
    private String messageTitle;

    /**
     * 消息类型(1系统 2私人 3通知)
     */
    private String messageType;

    /**
     * 已读状态(0未读 1已读)
     */
    private String readStatus;

    /**
     * 发送人ID
     */
    private Long senderId;

    /**
     * 优先级(0普通 1重要 2紧急)
     */
    private String priority;
}