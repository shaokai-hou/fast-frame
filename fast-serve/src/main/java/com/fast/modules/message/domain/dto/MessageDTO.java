package com.fast.modules.message.domain.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 消息DTO
 *
 * @author fast-frame
 */
@Data
public class MessageDTO {

    /**
     * 消息ID
     */
    private Long id;

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
     * 优先级(0普通 1重要 2紧急)
     */
    private String priority;

    /**
     * 状态(0正常 1撤回)
     */
    private String status;
}