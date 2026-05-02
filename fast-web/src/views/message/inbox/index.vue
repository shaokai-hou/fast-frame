<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" :visible="showSearch" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="消息标题" prop="messageTitle">
        <el-input v-model="queryParams.messageTitle" placeholder="请输入消息标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="消息类型" prop="messageType">
        <el-select v-model="queryParams.messageType" placeholder="全部" clearable>
          <el-option label="系统消息" value="1" />
          <el-option label="私人消息" value="2" />
          <el-option label="通知" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="已读状态" prop="readStatus">
        <el-select v-model="queryParams.readStatus" placeholder="全部" clearable>
          <el-option label="未读" value="0" />
          <el-option label="已读" value="1" />
        </el-select>
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="primary" plain :icon="Plus" @click="handleSend" v-hasPermi="['message:send']">发送消息</el-button>
        <el-button type="success" plain :icon="Check" @click="handleReadAll"
          v-hasPermi="['message:read']">全部已读</el-button>
        <el-button type="danger" plain :icon="Delete" @click="handleDelete" :disabled="multiple"
          v-hasPermi="['message:delete']">删除</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="messageList" row-key="id" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center"
          :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="消息标题" prop="messageTitle" show-overflow-tooltip>
          <template #default="scope">
            <span :class="{ 'unread-title': scope.row.readStatus === '0' }">{{ scope.row.messageTitle }}</span>
          </template>
        </el-table-column>
        <el-table-column label="消息类型" prop="messageType" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.messageType === '1'" type="danger">系统消息</el-tag>
            <el-tag v-else-if="scope.row.messageType === '2'" type="success">私人消息</el-tag>
            <el-tag v-else type="info">通知</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发送人" prop="senderName" width="120" />
        <el-table-column label="优先级" prop="priority" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.priority === '2'" type="danger">紧急</el-tag>
            <el-tag v-else-if="scope.row.priority === '1'" type="warning">重要</el-tag>
            <el-tag v-else>普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="readStatus" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.readStatus === '0'" type="warning">未读</el-tag>
            <el-tag v-else type="success">已读</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发送时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="230" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)">查看</el-button>
            <el-button link type="primary" @click="handleMarkRead(scope.row)" v-if="scope.row.readStatus === '0'"
              v-hasPermi="['message:read']">标记已读</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)"
              v-hasPermi="['message:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>

    <!-- 查看消息对话框 -->
    <el-dialog title="消息详情" v-model="viewOpen" width="750px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="消息标题" :span="2">{{ viewData.messageTitle }}</el-descriptions-item>
        <el-descriptions-item label="消息类型">
          <el-tag v-if="viewData.messageType === '1'" type="danger">系统消息</el-tag>
          <el-tag v-else-if="viewData.messageType === '2'" type="success">私人消息</el-tag>
          <el-tag v-else type="info">通知</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag v-if="viewData.priority === '2'" type="danger">紧急</el-tag>
          <el-tag v-else-if="viewData.priority === '1'" type="warning">重要</el-tag>
          <el-tag v-else>普通</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送人">{{ viewData.senderName }}</el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ viewData.createTime }}</el-descriptions-item>
      </el-descriptions>
      <div class="message-content-wrapper">
        <div class="content-label">消息内容</div>
        <div class="content-body">
          <RichTextViewer :content="viewData.messageContent" />
        </div>
      </div>
    </el-dialog>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Check } from '@element-plus/icons-vue'
import { listInbox, getMessageDetail, markAsRead, markAllAsRead, deleteMessage } from '@/api/message/message'
import RichTextViewer from '@/components/RichTextViewer/index.vue'

const router = useRouter()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const multiple = ref(true)
const total = ref(0)
const messageList = ref([])
const viewOpen = ref(false)
const viewData = ref({})
const ids = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  messageTitle: undefined,
  messageType: undefined,
  readStatus: undefined
})

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listInbox(queryParams)
    messageList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.messageTitle = undefined
  queryParams.messageType = undefined
  queryParams.readStatus = undefined
  handleQuery()
}

// 多选
const handleSelectionChange = (selection) => {
  ids.value = selection.map((item) => item.id)
  multiple.value = !selection.length
}

// 发送消息
const handleSend = () => {
  router.push('/message/send')
}

// 查看（自动标记已读，WebSocket 会推送新未读数量）
const handleView = async (row) => {
  const res = await getMessageDetail(row.id)
  viewData.value = res.data
  viewOpen.value = true
  getList()
}

// 标记已读（WebSocket 会推送新未读数量）
const handleMarkRead = async (row) => {
  await markAsRead(row.id)
  ElMessage.success('已标记为已读')
  getList()
}

// 全部已读（WebSocket 会推送新未读数量）
const handleReadAll = async () => {
  await ElMessageBox.confirm('是否确认将所有未读消息标记为已读?', '提示', { type: 'warning' })
  await markAllAsRead()
  ElMessage.success('已全部标记为已读')
  getList()
}

// 删除
const handleDelete = async (row) => {
  const deleteIds = row.id || ids.value
  await ElMessageBox.confirm('是否确认删除选中的消息?', '警告', { type: 'warning' })
  await deleteMessage(deleteIds)
  ElMessage.success('删除成功')
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.unread-title {
  font-weight: bold;
  color: #409eff;
}

.message-content-wrapper {
  margin-top: 16px;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}

.content-label {
  padding: 8px 12px;
  background: var(--el-fill-color-light);
  font-weight: 500;
  color: var(--el-text-color-regular);
  border-bottom: 1px solid var(--el-border-color);
}

.content-body {
  padding: 16px;
  max-height: 400px;
  overflow-y: auto;
}
</style>