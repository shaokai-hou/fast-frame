<template>
  <el-dialog
    v-model="visible"
    :title="fileName"
    width="80%"
    top="5vh"
    :close-on-click-modal="false"
    destroy-on-close
    @close="handleClose"
  >
    <!-- 加载状态 -->
    <div
      v-if="loading"
      class="preview-loading"
    >
      <el-icon
        class="is-loading"
        :size="32"
      >
        <Loading />
      </el-icon>
      <span>正在加载文件...</span>
    </div>

    <!-- 错误状态 -->
    <div
      v-else-if="error"
      class="preview-error"
    >
      <el-icon
        :size="48"
        color="#f56c6c"
      >
        <WarningFilled />
      </el-icon>
      <p class="error-text">
        {{ error }}
      </p>
      <el-button
        type="primary"
        @click="handleDownload"
      >
        下载文件
      </el-button>
    </div>

    <!-- 不支持预览 -->
    <div
      v-else-if="!canPreview"
      class="preview-unsupported"
    >
      <el-icon
        :size="48"
        color="#909399"
      >
        <Document />
      </el-icon>
      <p class="unsupported-text">
        该文件类型不支持在线预览
      </p>
      <p class="file-type">
        {{ getFileTypeHint() }}
      </p>
      <el-button
        type="primary"
        @click="handleDownload"
      >
        下载文件
      </el-button>
    </div>

    <!-- 图片预览 -->
    <div
      v-else-if="previewType === 'image'"
      class="preview-image"
    >
      <el-image
        :src="previewSrc"
        fit="contain"
        :preview-src-list="[previewSrc]"
        :initial-index="0"
        infinite
        close-on-press-escape
      >
        <template #error>
          <div class="image-error">
            <el-icon :size="32">
              <Picture />
            </el-icon>
            <span>图片加载失败</span>
          </div>
        </template>
      </el-image>
    </div>

    <!-- PDF 预览 -->
    <div
      v-else-if="previewType === 'pdf'"
      class="preview-pdf"
    >
      <VueOfficePdf
        :src="previewSrc"
        @rendered="handleRendered"
        @error="handlePreviewError"
      />
    </div>

    <!-- Word 预览 -->
    <div
      v-else-if="previewType === 'docx'"
      class="preview-docx"
    >
      <VueOfficeDocx
        :src="previewSrc"
        @rendered="handleRendered"
        @error="handlePreviewError"
      />
    </div>

    <!-- Excel 预览 -->
    <div
      v-else-if="previewType === 'xlsx'"
      class="preview-xlsx"
    >
      <VueOfficeExcel
        :src="previewSrc"
        @rendered="handleRendered"
        @error="handlePreviewError"
      />
    </div>

    <!-- PowerPoint 预览 -->
    <div
      v-else-if="previewType === 'pptx'"
      class="preview-pptx"
    >
      <VueOfficePptx
        :src="previewSrc"
        @rendered="handleRendered"
        @error="handlePreviewError"
      />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { Loading, WarningFilled, Document, Picture } from '@element-plus/icons-vue'
import VueOfficePdf from '@vue-office/pdf'
import VueOfficeDocx from '@vue-office/docx'
import VueOfficeExcel from '@vue-office/excel'
import VueOfficePptx from '@vue-office/pptx'
import { getBlob } from '@/utils/request'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  src: {
    type: [String, Blob],
    default: ''
  },
  contentType: {
    type: String,
    default: ''
  },
  fileName: {
    type: String,
    default: '文件预览'
  }
})

// Emits
const emit = defineEmits(['update:visible', 'download'])

// 状态
const loading = ref(false)
const error = ref('')
const previewSrc = ref('')
const previewType = ref('')
const blobUrl = ref('')

// 双向绑定 visible
const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

// 判断是否可以预览
const canPreview = computed(() => {
  return ['image', 'pdf', 'docx', 'xlsx', 'pptx'].includes(previewType.value)
})

// 获取预览类型
const getPreviewType = (contentType, fileName) => {
  // 图片类型
  if (contentType?.startsWith('image/')) {
    return 'image'
  }

  // PDF
  if (contentType === 'application/pdf' || fileName?.toLowerCase().endsWith('.pdf')) {
    return 'pdf'
  }

  // Word (docx)
  if (
    contentType === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' ||
    fileName?.toLowerCase().endsWith('.docx')
  ) {
    return 'docx'
  }

  // Excel (xlsx, xls)
  if (
    contentType === 'application/vnd.ms-excel' ||
    contentType === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
    fileName?.toLowerCase().endsWith('.xlsx') ||
    fileName?.toLowerCase().endsWith('.xls')
  ) {
    return 'xlsx'
  }

  // PowerPoint (pptx)
  if (
    contentType === 'application/vnd.ms-powerpoint' ||
    contentType === 'application/vnd.openxmlformats-officedocument.presentationml.presentation' ||
    fileName?.toLowerCase().endsWith('.pptx')
  ) {
    return 'pptx'
  }

  return ''
}

// 获取文件类型提示
const getFileTypeHint = () => {
  const ext = props.fileName?.split('.').pop()?.toLowerCase() || ''
  const typeHints = {
    doc: '旧版 Word 文档 (.doc) 不支持预览，请下载后查看',
    ppt: '旧版 PowerPoint 文档 (.ppt) 不支持预览，请下载后查看',
    zip: '压缩文件',
    rar: '压缩文件',
    '7z': '压缩文件',
    exe: '可执行文件',
    dll: '系统文件',
    txt: '纯文本文件，建议下载查看'
  }
  return typeHints[ext] || `文件类型: ${props.contentType || ext}`
}

// 处理文件加载
const loadFile = async () => {
  if (!props.src) return

  loading.value = true
  error.value = ''
  previewType.value = getPreviewType(props.contentType, props.fileName)

  try {
    // 如果 src 是字符串 URL，需要通过认证获取 Blob
    if (typeof props.src === 'string' && props.src) {
      const blob = await getBlob(props.src)
      const url = URL.createObjectURL(blob)
      blobUrl.value = url
      previewSrc.value = url
    } else if (props.src instanceof Blob) {
      // 如果已经是 Blob，直接创建 URL
      const url = URL.createObjectURL(props.src)
      blobUrl.value = url
      previewSrc.value = url
    }
  } catch (err) {
    error.value = '文件加载失败，请检查网络连接'
  } finally {
    loading.value = false
  }
}

// 渲染完成
const handleRendered = () => {
  loading.value = false
}

// 预览错误
const handlePreviewError = (err) => {
  error.value = '文件预览失败，请下载后查看'
  loading.value = false
}

// 下载文件
const handleDownload = () => {
  emit('download')
  visible.value = false
}

// 关闭对话框
const handleClose = () => {
  // 清理 blob URL
  if (blobUrl.value) {
    URL.revokeObjectURL(blobUrl.value)
    blobUrl.value = ''
  }
  previewSrc.value = ''
  loading.value = false
  error.value = ''
}

// 监听 visible 变化，加载文件
watch(
  () => props.visible,
  (val) => {
    if (val) {
      loadFile()
    }
  },
  { immediate: true }
)
</script>

<style scoped lang="scss">
.preview-loading,
.preview-error,
.preview-unsupported {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 70vh;
  gap: 16px;
}

.preview-loading {
  color: var(--el-text-color-secondary);
}

.error-text,
.unsupported-text {
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.file-type {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.preview-image {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 70vh;
  width: 100%;

  .el-image {
    width: 100%;
    height: 100%;
  }

  :deep(.el-image__inner) {
    object-fit: contain;
    max-width: 100%;
    max-height: 100%;
  }

  .image-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    color: var(--el-text-color-secondary);
  }
}

.preview-pdf,
.preview-docx,
.preview-xlsx,
.preview-pptx {
  height: 70vh;
  overflow: auto;

  // vue-office 组件样式
  :deep(.vue-office-pdf),
  :deep(.vue-office-docx),
  :deep(.vue-office-excel),
  :deep(.vue-office-pptx) {
    width: 100%;
    height: 100%;
  }
}

// Excel 预览特殊样式
.preview-xlsx {
  :deep(.x-spreadsheet) {
    width: 100%;
    height: 100%;
  }
}
</style>