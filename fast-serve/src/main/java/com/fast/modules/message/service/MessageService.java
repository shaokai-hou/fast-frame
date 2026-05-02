package com.fast.modules.message.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.result.PageRequest;
import com.fast.modules.message.domain.dto.MessageSendDTO;
import com.fast.modules.message.domain.query.MessageQuery;
import com.fast.modules.message.domain.query.SentMessageQuery;
import com.fast.modules.message.domain.vo.MessageListVO;
import com.fast.modules.message.domain.vo.MessageVO;
import com.fast.modules.message.domain.vo.SentMessageVO;
import com.fast.modules.message.domain.vo.UnreadCountVO;

import java.util.List;

/**
 * 消息Service
 *
 * @author fast-frame
 */
public interface MessageService {

    /**
     * 发送消息（支持群发）
     *
     * @param dto 发送消息DTO
     */
    void sendMessage(MessageSendDTO dto);

    /**
     * 分页查询收件箱消息列表
     *
     * @param pageRequest 分页参数
     * @param query       查询条件
     * @return 消息列表
     */
    IPage<MessageListVO> pageInbox(PageRequest pageRequest, MessageQuery query);

    /**
     * 分页查询已发送消息列表
     *
     * @param pageRequest 分页参数
     * @param query       查询条件
     * @return 已发送消息列表
     */
    IPage<SentMessageVO> pageSent(PageRequest pageRequest, SentMessageQuery query);

    /**
     * 查看消息详情（同时标记已读）
     *
     * @param id 消息ID
     * @return 消息详情
     */
    MessageVO getMessageDetail(Long id);

    /**
     * 标记消息已读
     *
     * @param id 消息ID
     */
    void markAsRead(Long id);

    /**
     * 标记所有消息已读
     */
    void markAllAsRead();

    /**
     * 删除消息（接收人删除关联）
     *
     * @param ids 消息ID列表
     */
    void deleteMessages(List<Long> ids);

    /**
     * 获取未读消息数量
     *
     * @return 未读消息统计
     */
    UnreadCountVO getUnreadCount();
}