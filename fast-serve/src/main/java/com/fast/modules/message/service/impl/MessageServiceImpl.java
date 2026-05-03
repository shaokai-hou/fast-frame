package com.fast.modules.message.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fast.common.constant.Constants;
import com.fast.common.exception.BusinessException;
import com.fast.framework.websocket.MessageWebSocketHandler;
import com.fast.modules.message.domain.dto.MessageSendDTO;
import com.fast.modules.message.domain.entity.Message;
import com.fast.modules.message.domain.entity.MessageUser;
import com.fast.modules.message.domain.query.MessageQuery;
import com.fast.modules.message.domain.query.SentMessageQuery;
import com.fast.modules.message.domain.vo.MessageListVO;
import com.fast.modules.message.domain.vo.MessageVO;
import com.fast.modules.message.domain.vo.SentMessageVO;
import com.fast.modules.message.domain.vo.UnreadCountVO;
import com.fast.modules.message.mapper.MessageMapper;
import com.fast.modules.message.mapper.MessageUserMapper;
import com.fast.modules.message.service.MessageService;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 消息Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final MessageUserMapper messageUserMapper;
    private final UserMapper userMapper;
    private final MessageWebSocketHandler webSocketHandler;

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 发送消息（支持群发）
     *
     * @param dto 发送消息DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(MessageSendDTO dto) {
        Long senderId = getCurrentUserId();

        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setSenderId(senderId);
        message.setStatus(Constants.NORMAL);
        if (message.getPriority() == null) {
            message.setPriority("0");
        }
        messageMapper.insert(message);

        for (Long receiverId : dto.getReceiverIds()) {
            MessageUser messageUser = new MessageUser();
            messageUser.setMessageId(message.getId());
            messageUser.setReceiverId(receiverId);
            messageUser.setReadStatus("0");
            messageUserMapper.insert(messageUser);
        }

        User sender = userMapper.selectById(senderId);
        String senderName = sender != null ? sender.getNickname() : "未知";

        webSocketHandler.broadcastNewMessage(
            dto.getReceiverIds(),
            message.getId(),
            dto.getMessageTitle(),
            senderName,
            dto.getPriority()
        );

        // 给每个接收人推送新的未读数量
        for (Long receiverId : dto.getReceiverIds()) {
            pushUnreadCount(receiverId);
        }
    }

    /**
     * 分页查询收件箱消息列表
     *
     * @param query 查询条件
     * @return 消息列表
     */
    @Override
    public IPage<MessageListVO> pageInbox(MessageQuery query) {
        Long receiverId = getCurrentUserId();
        return messageMapper.selectInboxPage(Page.of(query.getPageNum(), query.getPageSize()), query, receiverId);
    }

    /**
     * 分页查询已发送消息列表
     *
     * @param query 查询条件
     * @return 已发送消息列表
     */
    @Override
    public IPage<SentMessageVO> pageSent(SentMessageQuery query) {
        Long senderId = getCurrentUserId();
        return messageMapper.selectSentPage(Page.of(query.getPageNum(), query.getPageSize()), query, senderId);
    }

    /**
     * 查看消息详情（同时标记已读）
     *
     * @param id 消息ID
     * @return 消息详情
     */
    @Override
    public MessageVO getMessageDetail(Long id) {
        Long receiverId = getCurrentUserId();
        MessageVO messageVO = messageMapper.selectMessageDetail(id, receiverId);
        if (Objects.isNull(messageVO)) {
            throw new BusinessException("消息不存在或无权查看");
        }

        if ("0".equals(messageVO.getReadStatus())) {
            markAsRead(id);
            messageVO.setReadStatus("1");
            messageVO.setReadTime(LocalDateTime.now());
        }

        return messageVO;
    }

    /**
     * 标记消息已读
     *
     * @param id 消息ID
     */
    @Override
    public void markAsRead(Long id) {
        Long receiverId = getCurrentUserId();
        messageUserMapper.markAsRead(id, receiverId, LocalDateTime.now());
        // 推送新的未读数量
        pushUnreadCount(receiverId);
    }

    /**
     * 标记所有消息已读
     */
    @Override
    public void markAllAsRead() {
        Long receiverId = getCurrentUserId();
        messageUserMapper.markAllAsRead(receiverId, LocalDateTime.now());
        // 推送新的未读数量
        pushUnreadCount(receiverId);
    }

    /**
     * 推送未读数量给指定用户
     *
     * @param userId 用户ID
     */
    private void pushUnreadCount(Long userId) {
        UnreadCountVO unreadCount = messageMapper.selectUnreadCount(userId);
        webSocketHandler.sendUnreadCountUpdate(userId, unreadCount.getTotal());
    }

    /**
     * 删除消息（接收人删除关联）
     *
     * @param ids 消息ID列表
     */
    @Override
    public void deleteMessages(List<Long> ids) {
        Long receiverId = getCurrentUserId();
        for (Long id : ids) {
            LambdaUpdateWrapper<MessageUser> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MessageUser::getMessageId, id)
                   .eq(MessageUser::getReceiverId, receiverId)
                   .set(MessageUser::getDelFlag, Constants.DEL);
            messageUserMapper.update(null, wrapper);
        }
    }

    /**
     * 获取未读消息数量
     *
     * @return 未读消息统计
     */
    @Override
    public UnreadCountVO getUnreadCount() {
        Long receiverId = getCurrentUserId();
        return messageMapper.selectUnreadCount(receiverId);
    }
}