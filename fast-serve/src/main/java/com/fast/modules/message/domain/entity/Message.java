package com.fast.modules.message.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
public class Message extends BaseEntity {

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
     * 优先级(0普通 1重要 2紧急)
     */
    private String priority;

    /**
     * 状态(0正常 1撤回)
     */
    private String status;
}