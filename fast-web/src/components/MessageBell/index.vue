<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <div class="action-btn">
      <el-badge v-if="messageStore.unreadCount > 0" :value="messageStore.unreadCount" :max="99" class="message-badge">
        <el-icon :size="18">
          <Bell />
        </el-icon>
      </el-badge>
      <el-icon v-else :size="18">
        <Bell />
      </el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="inbox">
          <span>收件箱</span>
          <el-badge v-if="messageStore.unreadCount > 0" :value="messageStore.unreadCount" :max="99" class="item-badge" />
        </el-dropdown-item>
        <el-dropdown-item command="sent">发件箱</el-dropdown-item>
        <el-dropdown-item command="send">发送消息</el-dropdown-item>
        <el-dropdown-item command="readAll" :disabled="messageStore.unreadCount === 0">全部已读</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { useMessageStore } from '@/store/message'
import { markAllAsRead } from '@/api/message/message'

const router = useRouter()
const messageStore = useMessageStore()

// 处理菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'inbox':
      router.push('/message/inbox')
      break
    case 'sent':
      router.push('/message/sent')
      break
    case 'send':
      router.push('/message/send')
      break
    case 'readAll':
      await markAllAsRead()
      ElMessage.success('已全部标记为已读')
      break
  }
}

onMounted(() => {
  messageStore.fetchUnreadCount()
  messageStore.connectWebSocket()
})

onUnmounted(() => {
  messageStore.disconnectWebSocket()
})
</script>

<style scoped>
.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  cursor: pointer;
  color: var(--color-foreground-muted);
  transition: all 0.2s ease;
  border-radius: 10px;

  &:hover {
    color: var(--color-primary);
    background: var(--color-primary-lighter);
  }
}

.message-badge {
  cursor: pointer;
}

.item-badge {
  margin-left: 8px;
}
</style>