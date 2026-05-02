package com.fast.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.message.domain.entity.MessageUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息用户关联Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface MessageUserMapper extends BaseMapper<MessageUser> {

    /**
     * 批量插入消息用户关联
     *
     * @param list 消息用户关联列表
     */
    void batchInsert(@Param("list") List<MessageUser> list);

    /**
     * 标记消息已读
     *
     * @param messageId  消息ID
     * @param receiverId 接收人ID
     * @param readTime   阅读时间
     */
    void markAsRead(@Param("messageId") Long messageId, @Param("receiverId") Long receiverId, @Param("readTime") LocalDateTime readTime);

    /**
     * 标记所有消息已读
     *
     * @param receiverId 接收人ID
     * @param readTime   阅读时间
     */
    void markAllAsRead(@Param("receiverId") Long receiverId, @Param("readTime") LocalDateTime readTime);

    /**
     * 查询消息接收人ID列表
     *
     * @param messageId 消息ID
     * @return 接收人ID列表
     */
    List<Long> selectReceiverIds(@Param("messageId") Long messageId);
}