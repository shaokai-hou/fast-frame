package com.fast.modules.message.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 已发送消息查询条件
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SentMessageQuery extends PageRequest {

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