package com.fast.modules.message.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 已发送消息列表VO（发件箱）
 *
 * @author fast-frame
 */
@Data
public class SentMessageVO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 消息标题
     */
    private String messageTitle;

    /**
     * 消息类型(1系统 2私人 3通知)
     */
    private String messageType;

    /**
     * 接收人数量
     */
    private Integer receiverCount;

    /**
     * 已读数量
     */
    private Integer readCount;

    /**
     * 优先级(0普通 1重要 2紧急)
     */
    private String priority;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}