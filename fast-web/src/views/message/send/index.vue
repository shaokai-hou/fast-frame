<template>
  <PageContainer>
    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 800px"
      >
        <el-form-item
          label="消息标题"
          prop="messageTitle"
        >
          <el-input
            v-model="form.messageTitle"
            placeholder="请输入消息标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item
          label="消息类型"
          prop="messageType"
        >
          <el-select
            v-model="form.messageType"
            placeholder="请选择消息类型"
          >
            <el-option
              label="系统消息"
              value="1"
            />
            <el-option
              label="私人消息"
              value="2"
            />
            <el-option
              label="通知"
              value="3"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="接收人"
          prop="receiverIds"
        >
          <el-select
            v-model="form.receiverIds"
            multiple
            filterable
            placeholder="请选择接收人"
            style="width: 100%"
          >
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.nickname"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="优先级"
          prop="priority"
        >
          <el-select
            v-model="form.priority"
            placeholder="请选择优先级"
          >
            <el-option
              label="普通"
              value="0"
            />
            <el-option
              label="重要"
              value="1"
            />
            <el-option
              label="紧急"
              value="2"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          label="消息内容"
          prop="messageContent"
        >
          <RichTextEditor
            v-model="form.messageContent"
            placeholder="请输入消息内容"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
          >
            发送
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
          <el-button @click="handleBack">
            返回
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </PageContainer>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendMessage } from '@/api/message/message'
import { listAllUser } from '@/api/system/user'
import RichTextEditor from '@/components/RichTextEditor/index.vue'

const router = useRouter()
const { proxy } = getCurrentInstance()

// 数据
const submitLoading = ref(false)
const userList = ref([])

// 表单
const form = ref({
  messageTitle: undefined,
  messageType: '2',
  receiverIds: [],
  priority: '0',
  messageContent: undefined
})

// 校验规则
const rules = {
  messageTitle: [{ required: true, message: '消息标题不能为空', trigger: 'blur' }],
  messageType: [{ required: true, message: '消息类型不能为空', trigger: 'change' }],
  receiverIds: [{ required: true, message: '接收人不能为空', trigger: 'change' }]
}

// 获取用户列表
const getUserList = async () => {
  const res = await listAllUser()
  userList.value = res.data
}

// 发送
const handleSubmit = async () => {
  await proxy.$refs.formRef.validate()
  submitLoading.value = true
  try {
    await sendMessage(form.value)
    ElMessage.success('发送成功')
    handleReset()
  } finally {
    submitLoading.value = false
  }
}

// 重置
const handleReset = () => {
  form.value = {
    messageTitle: undefined,
    messageType: '2',
    receiverIds: [],
    priority: '0',
    messageContent: undefined
  }
  proxy.$refs.formRef?.resetFields()
}

// 返回
const handleBack = () => {
  router.push('/message/inbox')
}

onMounted(() => {
  getUserList()
})
</script>