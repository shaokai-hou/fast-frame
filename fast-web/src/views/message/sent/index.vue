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
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-button type="primary" plain :icon="Plus" @click="handleSend" v-hasPermi="['message:send']">发送消息</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="messageList" row-key="id">
        <el-table-column type="index" label="序号" width="60" align="center"
          :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
        <el-table-column label="消息标题" prop="messageTitle" show-overflow-tooltip />
        <el-table-column label="消息类型" prop="messageType" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.messageType === '1'" type="danger">系统消息</el-tag>
            <el-tag v-else-if="scope.row.messageType === '2'" type="success">私人消息</el-tag>
            <el-tag v-else type="info">通知</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="接收人数" prop="receiverCount" width="100" align="center" />
        <el-table-column label="已读人数" prop="readCount" width="100" align="center">
          <template #default="scope">
            <span>{{ scope.row.readCount }} / {{ scope.row.receiverCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="优先级" prop="priority" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.priority === '2'" type="danger">紧急</el-tag>
            <el-tag v-else-if="scope.row.priority === '1'" type="warning">重要</el-tag>
            <el-tag v-else>普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发送时间" prop="createTime" width="180" />
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getList" />
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { listSent } from '@/api/message/message'

const router = useRouter()
const { proxy } = getCurrentInstance()

// 数据
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const messageList = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  messageTitle: undefined,
  messageType: undefined
})

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listSent(queryParams)
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
  handleQuery()
}

// 发送消息
const handleSend = () => {
  router.push('/message/send')
}

onMounted(() => {
  getList()
})
</script>