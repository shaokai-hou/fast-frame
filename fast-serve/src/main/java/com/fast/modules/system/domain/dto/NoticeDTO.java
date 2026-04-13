package com.fast.modules.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 通知公告DTO
 *
 * @author fast-frame
 */
@Data
public class NoticeDTO {

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 100, message = "公告标题长度不能超过100个字符")
    private String noticeTitle;

    /**
     * 公告类型(1通知 2公告)
     */
    @NotBlank(message = "公告类型不能为空")
    private String noticeType;

    /**
     * 公告内容
     */
    private String noticeContent;

    /**
     * 状态(0正常 1关闭)
     */
    private String status;
}