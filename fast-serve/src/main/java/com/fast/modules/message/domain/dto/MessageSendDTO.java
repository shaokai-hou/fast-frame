package com.fast.modules.message.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 发送消息DTO
 *
 * @author fast-frame
 */
@Data
public class MessageSendDTO {

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    private String messageTitle;

    /**
     * 消息类型(1系统 2私人 3通知)
     */
    @NotBlank(message = "消息类型不能为空")
    private String messageType;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 接收人ID列表（支持群发）
     */
    @NotNull(message = "接收人不能为空")
    private List<Long> receiverIds;

    /**
     * 优先级(0普通 1重要 2紧急)
     */
    private String priority;
}