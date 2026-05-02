package com.fast.modules.message.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 消息用户关联实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message_user")
public class MessageUser extends BaseEntity {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 接收人ID
     */
    private Long receiverId;

    /**
     * 已读状态(0未读 1已读)
     */
    private String readStatus;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
}