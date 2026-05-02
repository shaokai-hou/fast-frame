package com.fast.modules.message.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息详情VO
 *
 * @author fast-frame
 */
@Data
public class MessageVO {

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
     * 消息内容
     */
    private String messageContent;

    /**
     * 发送人ID
     */
    private Long senderId;

    /**
     * 发送人名称
     */
    private String senderName;

    /**
     * 优先级(0普通 1重要 2紧急)
     */
    private String priority;

    /**
     * 已读状态(0未读 1已读)
     */
    private String readStatus;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}