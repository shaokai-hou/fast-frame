<template>
  <PageContainer>
    <!-- 搜索栏 -->
    <SearchBar :model="queryParams" @search="handleQuery" @reset="resetQuery">
      <el-form-item label="文件名" prop="fileName">
        <el-input v-model="queryParams.fileName" placeholder="请输入文件名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
    </SearchBar>

    <!-- 内容卡片 -->
    <div class="content-card">
      <!-- 工具栏 -->
      <div class="tool-bar">
        <el-upload
          class="upload-demo"
          action="#"
          :http-request="handleUpload"
          :show-file-list="false"
          multiple
        >
          <el-button type="primary" plain :icon="Upload">上传文件</el-button>
        </el-upload>
      </div>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="fileList" row-key="id">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="文件名" prop="originalFilename" min-width="200">
          <template #default="scope">
            {{ scope.row.originalFilename || scope.row.fileName }}
          </template>
        </el-table-column>
        <el-table-column label="文件类型" prop="contentType" width="120">
          <template #default="scope">
            <el-tag :type="getFileTypeTag(scope.row.contentType)">
              {{ getFileTypeName(scope.row.contentType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" prop="size" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.size) }}
          </template>
        </el-table-column>
        <el-table-column label="上传时间" prop="uploadTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="handlePreview(scope.row)">预览</el-button>
            <el-button link type="success" @click="handleDownload(scope.row)">下载</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['system:file:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize" @pagination="getFileList" />
    </div>

    <!-- 文件预览组件 -->
    <FilePreview
      v-model:visible="previewVisible"
      :src="previewFile.src"
      :content-type="previewFile.contentType"
      :file-name="previewFile.fileName"
      @download="handlePreviewDownload"
    />
  </PageContainer>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Upload } from '@element-plus/icons-vue'
import { listFile, uploadFile, downloadFile, deleteFile } from '@/api/system/file'
import FilePreview from '@/components/FilePreview/index.vue'

// 数据
const loading = ref(false)
const fileList = ref([])
const total = ref(0)

// 预览状态
const previewVisible = ref(false)
const previewFile = ref({
  src: '',
  contentType: '',
  fileName: ''
})

// 查询参数
const queryParams = reactive({
  fileName: undefined,
  pageNum: 1,
  pageSize: 10
})

// 获取文件列表
const getFileList = async () => {
  loading.value = true
  try {
    const res = await listFile(queryParams)
    fileList.value = res.data.records
    total.value = res.data.total
  } catch {
    ElMessage.error('获取文件列表失败')
  } finally {
    loading.value = false
  }
}

// 获取文件类型
const getFileType = (contentType) => {
  if (!contentType) return 'other'
  if (contentType.startsWith('image/')) return 'image'
  if (contentType.startsWith('video/')) return 'video'
  if (contentType.includes('pdf') || contentType.includes('document') || contentType.includes('word') || contentType.includes('excel') || contentType.includes('text')) return 'document'
  return 'other'
}

// 获取文件类型名称
const getFileTypeName = (contentType) => {
  const type = getFileType(contentType)
  const typeNames = {
    image: '图片',
    video: '视频',
    document: '文档',
    other: '其他'
  }
  return typeNames[type] || '其他'
}

// 获取文件类型标签
const getFileTypeTag = (contentType) => {
  const type = getFileType(contentType)
  const typeTags = {
    image: 'success',
    video: 'warning',
    document: 'primary',
    other: 'info'
  }
  return typeTags[type] || 'info'
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getFileList()
}

// 重置
const resetQuery = () => {
  queryParams.fileName = undefined
  queryParams.pageNum = 1
  getFileList()
}

// 上传文件
const handleUpload = async (options) => {
  try {
    loading.value = true
    await uploadFile(options.file)
    ElMessage.success('上传成功')
    getFileList()
  } catch {
    ElMessage.error('上传失败')
  } finally {
    loading.value = false
  }
}

// 预览
const handlePreview = (row) => {
  previewFile.value = {
    src: `/system/file/download/${row.fileName}`,
    contentType: row.contentType,
    fileName: row.originalFilename || row.fileName
  }
  previewVisible.value = true
}

// 预览下载回调
const handlePreviewDownload = async () => {
  const file = previewFile.value
  // 从 src 中提取 objectName
  const objectName = file.src.replace('/system/file/download/', '')
  try {
    await downloadFile(objectName, file.fileName)
  } catch {
    ElMessage.error('下载失败')
  }
}

// 下载
const handleDownload = async (row) => {
  try {
    await downloadFile(row.fileName, row.originalFilename || row.fileName)
  } catch {
    ElMessage.error('下载失败')
  }
}

// 删除
const handleDelete = async (row) => {
  await ElMessageBox.confirm('是否确认删除该文件?', '警告', { type: 'warning' })
  await deleteFile(row.id)
  ElMessage.success('删除成功')
  getFileList()
}

// 初始化
onMounted(() => {
  getFileList()
})
</script>

