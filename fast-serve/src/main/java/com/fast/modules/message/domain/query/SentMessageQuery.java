package com.fast.modules.message.domain.query;

import lombok.Data;

/**
 * 已发送消息查询条件
 *
 * @author fast-frame
 */
@Data
public class SentMessageQuery {

    /**
     * 消息标题
     */
    private String messageTitle;

    /**
     * 消息类型(1系统 2私人 3通知)
     */
    private String messageType;

    /**
     * 优先级(0普通 1重要 2紧急)
     */
    private String priority;
}