<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item label="文件名" prop="fileName">
          <el-input v-model="queryParams.fileName" placeholder="请输入文件名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

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
      <el-table v-loading="loading" :data="fileList">
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
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </div>

    <!-- 文件预览组件 -->
    <FilePreview
      v-model:visible="previewVisible"
      :src="previewFile.src"
      :content-type="previewFile.contentType"
      :file-name="previewFile.fileName"
      @download="handlePreviewDownload"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Upload } from '@element-plus/icons-vue'
import { listFile, uploadFile, downloadFile, deleteFile } from '@/api/file'
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
    src: `/file/download/${row.fileName}`,
    contentType: row.contentType,
    fileName: row.originalFilename || row.fileName
  }
  previewVisible.value = true
}

// 预览下载回调
const handlePreviewDownload = async () => {
  const file = previewFile.value
  // 从 src 中提取 objectName
  const objectName = file.src.replace('/file/download/', '')
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

<style scoped lang="scss">
.page-container {
  min-height: 100%;
}

.search-bar {
  background: var(--color-surface);
  padding: 20px 24px;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);

  :deep(.el-form-item) {
    margin-bottom: 0;
  }

  :deep(.el-input),
  :deep(.el-select) {
    width: 200px;
  }
}

.content-card {
  background: var(--color-surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
  border: 1px solid var(--color-border-light);
}

.tool-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>