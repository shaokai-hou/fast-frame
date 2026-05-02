package com.fast.modules.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.message.domain.dto.MessageSendDTO;
import com.fast.modules.message.domain.query.MessageQuery;
import com.fast.modules.message.domain.query.SentMessageQuery;
import com.fast.modules.message.domain.vo.MessageListVO;
import com.fast.modules.message.domain.vo.MessageVO;
import com.fast.modules.message.domain.vo.SentMessageVO;
import com.fast.modules.message.domain.vo.UnreadCountVO;
import com.fast.modules.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 消息Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController extends BaseController {

    private final MessageService messageService;

    /**
     * 发送消息（支持群发）
     *
     * @param dto 发送消息DTO
     * @return 成功结果
     */
    @SaCheckPermission("message:send")
    @Log(title = "消息", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public Result<Void> send(@Validated @RequestBody MessageSendDTO dto) {
        messageService.sendMessage(dto);
        return success();
    }

    /**
     * 分页查询收件箱消息列表
     *
     * @param pageRequest 分页参数
     * @param query       查询条件
     * @return 消息列表
     */
    @SaCheckPermission("message:page")
    @GetMapping("/inbox")
    public Result<IPage<MessageListVO>> inbox(PageRequest pageRequest, MessageQuery query) {
        return success(messageService.pageInbox(pageRequest, query));
    }

    /**
     * 分页查询已发送消息列表
     *
     * @param pageRequest 分页参数
     * @param query       查询条件
     * @return 已发送消息列表
     */
    @SaCheckPermission("message:sent")
    @GetMapping("/sent")
    public Result<IPage<SentMessageVO>> sent(PageRequest pageRequest, SentMessageQuery query) {
        return success(messageService.pageSent(pageRequest, query));
    }

    /**
     * 查看消息详情（同时标记已读）
     *
     * @param id 消息ID
     * @return 消息详情
     */
    @SaCheckPermission("message:detail")
    @GetMapping("/{id}")
    public Result<MessageVO> detail(@PathVariable Long id) {
        return success(messageService.getMessageDetail(id));
    }

    /**
     * 标记消息已读
     *
     * @param id 消息ID
     * @return 成功结果
     */
    @SaCheckPermission("message:read")
    @PutMapping("/read/{id}")
    public Result<Void> read(@PathVariable Long id) {
        messageService.markAsRead(id);
        return success();
    }

    /**
     * 标记所有消息已读
     *
     * @return 成功结果
     */
    @SaCheckPermission("message:read")
    @PutMapping("/readAll")
    public Result<Void> readAll() {
        messageService.markAllAsRead();
        return success();
    }

    /**
     * 删除消息（接收人删除关联）
     *
     * @param ids 消息ID数组
     * @return 成功结果
     */
    @SaCheckPermission("message:delete")
    @Log(title = "消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        messageService.deleteMessages(idList);
        return success();
    }

    /**
     * 获取未读消息数量
     *
     * @return 未读消息统计
     */
    @GetMapping("/unreadCount")
    public Result<UnreadCountVO> unreadCount() {
        return success(messageService.getUnreadCount());
    }
}