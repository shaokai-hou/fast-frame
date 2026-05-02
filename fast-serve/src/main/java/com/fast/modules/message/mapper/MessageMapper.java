package com.fast.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.message.domain.entity.Message;
import com.fast.modules.message.domain.query.MessageQuery;
import com.fast.modules.message.domain.query.SentMessageQuery;
import com.fast.modules.message.domain.vo.MessageListVO;
import com.fast.modules.message.domain.vo.MessageVO;
import com.fast.modules.message.domain.vo.SentMessageVO;
import com.fast.modules.message.domain.vo.UnreadCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 分页查询收件箱消息列表
     *
     * @param page       分页参数
     * @param query      查询条件
     * @param receiverId 接收人ID
     * @return 消息列表
     */
    IPage<MessageListVO> selectInboxPage(IPage<MessageListVO> page, @Param("query") MessageQuery query, @Param("receiverId") Long receiverId);

    /**
     * 分页查询已发送消息列表
     *
     * @param page    分页参数
     * @param query   查询条件
     * @param senderId 发送人ID
     * @return 已发送消息列表
     */
    IPage<SentMessageVO> selectSentPage(IPage<SentMessageVO> page, @Param("query") SentMessageQuery query, @Param("senderId") Long senderId);

    /**
     * 查询消息详情
     *
     * @param id         消息ID
     * @param receiverId 接收人ID
     * @return 消息详情
     */
    MessageVO selectMessageDetail(@Param("id") Long id, @Param("receiverId") Long receiverId);

    /**
     * 查询未读消息数量
     *
     * @param receiverId 接收人ID
     * @return 未读消息统计
     */
    UnreadCountVO selectUnreadCount(@Param("receiverId") Long receiverId);
}