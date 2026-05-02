<template>
  <!-- 导入对话框 -->
  <el-dialog
    v-model="visible"
    title="导入用户"
    width="480px"
    append-to-body
    @closed="resetImport"
  >
    <el-upload
      ref="uploadRef"
      :http-request="customUpload"
      :before-upload="beforeUpload"
      :auto-upload="false"
      accept=".xlsx"
      :limit="1"
      :file-list="fileList"
      :on-change="handleFileChange"
      :on-remove="handleFileRemove"
      drag
    >
      <el-icon class="el-icon--upload">
        <upload-filled />
      </el-icon>
      <div class="el-upload__text">
        将文件拖到此处，或 <em>点击上传</em>
      </div>
      <template #tip>
        <div class="upload-tip">
          <span
            class="link"
            @click="handleDownloadTemplate"
          >下载模板</span>
          <span>仅允许 .xlsx 文件</span>
        </div>
      </template>
    </el-upload>
    <template #footer>
      <el-button @click="visible = false">
        取消
      </el-button>
      <el-button
        type="primary"
        :loading="importLoading"
        :disabled="fileList.length === 0"
        @click="submitUpload"
      >
        开始导入
      </el-button>
    </template>
  </el-dialog>

  <!-- 导入结果对话框 -->
  <el-dialog
    v-model="resultVisible"
    title="导入结果"
    width="480px"
    append-to-body
    class="result-dialog"
  >
    <div class="result-content">
      <div class="result-stats">
        <span class="stat-item success">成功 <strong>{{ resultData.successCount }}</strong> 条</span>
        <span class="stat-item error">失败 <strong>{{ resultData.errorCount }}</strong> 条</span>
      </div>
      <el-table
        v-if="resultData.errorCount > 0"
        :data="parsedErrors"
        row-key="row"
        max-height="260"
        style="width: 100%; margin-top: 12px"
        border
        size="small"
      >
        <el-table-column
          prop="row"
          label="行号"
          width="70"
          align="center"
        />
        <el-table-column
          prop="message"
          label="错误原因"
          show-overflow-tooltip
        />
      </el-table>
    </div>
    <template #footer>
      <el-button
        type="primary"
        @click="resultVisible = false"
      >
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { importUser, downloadTemplate } from '@/api/system/user'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(props.modelValue)
const resultVisible = ref(false)
const uploadRef = ref(null)
const importLoading = ref(false)
const fileList = ref([])

const resultData = reactive({
  successCount: 0,
  errorCount: 0,
  errorMessages: []
})

// 解析错误信息
const parsedErrors = computed(() => {
  return resultData.errorMessages.map(msg => {
    const match = msg.match(/第 (\d+) 行：(.+)/)
    if (match) {
      return { row: match[1], message: match[2] }
    }
    return { row: '-', message: msg }
  })
})

// 监听 visible
watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 文件选择变化
const handleFileChange = (file, files) => {
  fileList.value = files
}

// 文件移除
const handleFileRemove = (file, files) => {
  fileList.value = files
}

// 文件上传前校验
const beforeUpload = (file) => {
  const isXlsx = file.name.endsWith('.xlsx')
  if (!isXlsx) {
    ElMessage.error('仅允许上传 .xlsx 文件')
    return false
  }
  return true
}

// 开始导入
const submitUpload = () => {
  uploadRef.value.submit()
}

// 自定义上传方法
const customUpload = async (options) => {
  importLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    const res = await importUser(formData)
    if (res.code === 200) {
      const { successCount, errorCount, errorMessages } = res.data
      visible.value = false
      resultData.successCount = successCount
      resultData.errorCount = errorCount
      resultData.errorMessages = errorMessages
      resultVisible.value = true
      emit('success')
    } else {
      ElMessage.error(res.msg || '导入失败')
    }
  } catch {
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
}

// 下载模板
const handleDownloadTemplate = async () => {
  try {
    const res = await downloadTemplate()
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = '用户导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(link.href)
  } catch {
    ElMessage.error('下载模板失败')
  }
}

// 重置导入
const resetImport = () => {
  fileList.value = []
}
</script>

<style scoped lang="scss">
.result-content {
  .result-stats {
    display: flex;
    gap: 24px;
    font-size: 14px;

    .stat-item {
      &.success strong {
        color: #67c23a;
      }

      &.error strong {
        color: #e6a23c;
      }
    }
  }
}

.upload-tip {
  color: #909399;
  font-size: 13px;
  display: flex;
  gap: 8px;
  margin-top: 12px;

  .link {
    color: #409eff;
    cursor: pointer;

    &:hover {
      color: #66b1ff;
    }
  }
}
</style>